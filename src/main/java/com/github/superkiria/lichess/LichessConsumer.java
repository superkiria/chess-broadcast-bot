package com.github.superkiria.lichess;

import com.github.superkiria.cbbot.broadcast.BroadcastsKeeper;
import com.github.superkiria.lichess.model.LichessEvent;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LichessConsumer {

    private final static Logger LOG = LoggerFactory.getLogger(LichessConsumer.class);
    private final WebClient webClient;
    private final BroadcastsKeeper keeper;

    private Date lastCall = new Date(0);
    private Date eventsCacheLastCall = new Date(0);
    private List<LichessEvent> eventsCache;
    private Disposable subscription;

    @Autowired
    public LichessConsumer(WebClient webClient, BroadcastsKeeper keeper) {
        this.webClient = webClient;
        this.keeper = keeper;
    }

    @SneakyThrows
    public void subscribeForRound(String round) {
        synchronized (this) {
            if (subscription != null) {
                subscription.dispose();
                subscription = null;
            }
            long now  = new Date().getTime();
            if (now - lastCall.getTime() < 61000) {
                Thread.sleep(61000 - now + lastCall.getTime());
            }
            lastCall = new Date();
            subscription = webClient.get()
                    .uri("https://lichess.org/api/stream/broadcast/round/{round}.pgn", round)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnSubscribe(o -> LOG.info("Subscibed for lichess broadcast: {}", o.toString()))
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(60)))
                    .doOnError(Exception.class, e -> LOG.error("Round" + round, e))
                    .subscribe(s -> this.keeper.getChatBroadcasters(round).forEach(chatBroadcaster -> chatBroadcaster.addPartOfPgn(s)));
        }
    }

    @SneakyThrows
    public List<LichessEvent> getLichessBroadcasts() {
        long now  = new Date().getTime();
        if (eventsCache != null && now - eventsCacheLastCall.getTime() < 1200000) {
            LOG.info("getLichessBroadcasts from cache");
            return eventsCache;
        }
        synchronized (this) {
            now  = new Date().getTime();
            if (now - lastCall.getTime() < 61000) {
                Thread.sleep(610000 - now + lastCall.getTime());
            }
            lastCall = new Date();
            eventsCacheLastCall = new Date();
            LOG.info("getLichessBroadcasts");
            eventsCache = webClient.get()
                        .uri("https://lichess.org/api/broadcast")
                        .retrieve()
                        .bodyToFlux(LichessEvent.class)
                        .collect(Collectors.toList()).block();
            return eventsCache;
        }
    }

}
