package com.github.superkiria.cbbot.lichess;

import com.github.superkiria.cbbot.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.processing.PgnQueue;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LichessConsumer {

    private final static Logger LOG = LoggerFactory.getLogger(LichessConsumer.class);

    @Value("${lichess.stream.endpoint}")
    private String streamEndpoint;

    @Value("${lichess.broadcasts.endpoint}")
    private String broadcastsEndpoint;

    private final WebClient webClient;
    private final PgnQueue queue;

    private Date lastCall = new Date(0);
    private Date eventsCacheLastCall = new Date(0);
    private List<LichessEvent> eventsCache;
    private Disposable subscription;

    private String currentSubscriptionRoundId = null;

    @Autowired
    public LichessConsumer(WebClient webClient, PgnQueue queue) {
        this.webClient = webClient;
        this.queue = queue;
    }

    @SneakyThrows
    public void subscribeForRound(String round) {
        if (round == null) {
            return;
        }
        synchronized (this) {
            if (subscription != null) {
                subscription.dispose();
                subscription = null;
                LOG.info("subscription.dispose();");
            }
            long now  = new Date().getTime();
            if (now - lastCall.getTime() < 60000) {
                Thread.sleep(60000 - now + lastCall.getTime());
            }
            lastCall = new Date();
            setCurrentSubscriptionRoundId(round);
            String uri = String.format("%s/%s.pgn", streamEndpoint, round);
            subscription = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnSubscribe(o -> LOG.info(uri))
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(60)))
                    .doOnError(Exception.class, e -> LOG.error("Round " + round, e))
                    .subscribe(this.queue::putPgnPart);
        }
    }

    public void cancelSubscription() {
        synchronized (this) {
            if (subscription != null) {
                LOG.info("Subscription for {} is cancelled", currentSubscriptionRoundId);
                subscription.dispose();
                subscription = null;
            }
            setCurrentSubscriptionRoundId(null);
        }
    }

    @SneakyThrows
    public List<LichessEvent> getLichessBroadcasts() {
        long now  = new Date().getTime();
        if (eventsCache != null && now - eventsCacheLastCall.getTime() < 600_000) {
            LOG.debug("getLichessBroadcasts from cache");
            return eventsCache;
        }
        synchronized (this) {
            eventsCacheLastCall = new Date();
            eventsCache = webClient.get()
                        .uri(broadcastsEndpoint)
                        .retrieve()
                        .bodyToFlux(LichessEvent.class)
                        .collect(Collectors.toList()).block();
            LOG.info("getLichessBroadcasts {}", eventsCache != null ? eventsCache.size() : null);
            return eventsCache;
        }
    }

    public List<LichessEvent> getActualLichessBroadcasts() {
        return getLichessBroadcasts().stream().filter(
                o -> o.getRounds().stream().anyMatch(r -> r.getFinished() == null)
        ).sorted(Comparator.comparing(o -> o.getTour().getName())).collect(Collectors.toList());
    }

    public LichessEvent getLichessEventById(String eventId) {
        List<LichessEvent> events = getLichessBroadcasts();
        Optional<LichessEvent> first = events.stream().filter(e -> e.getTour().getId().equals(eventId)).findFirst();
        return first.orElse(null);
    }

    public String getCurrentSubscriptionRoundId() {
        return currentSubscriptionRoundId;
    }

    public void setCurrentSubscriptionRoundId(String currentSubscriptionRoundId) {
        this.currentSubscriptionRoundId = currentSubscriptionRoundId;
    }
}
