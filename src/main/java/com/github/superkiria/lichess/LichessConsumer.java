package com.github.superkiria.lichess;

import com.github.superkiria.cbbot.broadcast.BroadcastsKeeper;
import com.github.superkiria.lichess.model.LichessEvent;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class LichessConsumer {

    private final static Logger LOG = LoggerFactory.getLogger(LichessConsumer.class);
    private final WebClient webClient;
    private final BroadcastsKeeper keeper;
    private final Map<String, RoundConsumer> broadcastConsumers = new ConcurrentHashMap<>();

    private Date lastCall = new Date(0);

    private Date eventsCacheLastCall = new Date(0);
    private List<LichessEvent> eventsCache;

    @Autowired
    public LichessConsumer(WebClient webClient, BroadcastsKeeper keeper) {
        this.webClient = webClient;
        this.keeper = keeper;
    }

    @SneakyThrows
    public RoundConsumer registerBroadcast(String round) {
        synchronized (this) {
            long now  = new Date().getTime();
            if (now - lastCall.getTime() < 310000) {
                Thread.sleep(310000 - now + lastCall.getTime());
            }
            lastCall = new Date();
            if (broadcastConsumers.get(round) == null) {
                broadcastConsumers.put(round, new RoundConsumer(webClient, round, keeper));
            }
        }
        return broadcastConsumers.get(round);
    }

    @SneakyThrows
    public List<LichessEvent> getLichessBroadcasts() {
        if (eventsCache != null) {
            LOG.info("getLichessBroadcasts from cache");
            return eventsCache;
        }
        synchronized (this) {
            long now  = new Date().getTime();
            if (now - lastCall.getTime() < 61000) {
                Thread.sleep(now - lastCall.getTime());
            }
            lastCall = new Date();
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
