package com.github.superkiria.lichess;

import com.github.superkiria.cbbot.broadcast.BroadcastsKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;

public class BroadcastConsumer {

    private final WebClient webClient = WebClient.create();
    private final static Logger LOG = LoggerFactory.getLogger(BroadcastConsumer.class);
    private final String round;
    private final Flux<String> flux;
    private final BroadcastsKeeper keeper;

    public BroadcastConsumer(String round, BroadcastsKeeper keeper) {
        this.round = round;
        flux = webClient.get()
                .uri("https://lichess.org/api/stream/broadcast/round/{round}.pgn", round)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnSubscribe(o -> LOG.info("Subscibed for lichess broadcast: {}", o.toString()))
                .retryWhen(Retry.fixedDelay(60, Duration.ofSeconds(60)))
                .doOnError(IOException.class, e -> LOG.error("Round" + round, e));
        this.keeper = keeper;
        flux.subscribe(s -> this.keeper.getChatBroadcasters(round).forEach(chatBroadcaster -> chatBroadcaster.addPartOfPgn(s)));
    }

}
