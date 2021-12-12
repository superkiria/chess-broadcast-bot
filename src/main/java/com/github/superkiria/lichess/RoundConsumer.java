package com.github.superkiria.lichess;

import com.github.superkiria.cbbot.broadcast.BroadcastsKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public class RoundConsumer {

    private final static Logger LOG = LoggerFactory.getLogger(RoundConsumer.class);
    private final String round;
    private final Flux<String> flux;
    private final BroadcastsKeeper keeper;

    public RoundConsumer(WebClient webClient, String round, BroadcastsKeeper keeper) {
        this.round = round;
        flux = webClient.get()
                .uri("https://lichess.org/api/stream/broadcast/round/{round}.pgn", round)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnSubscribe(o -> LOG.info("Subscibed for lichess broadcast: {}", o.toString()))
//                .retryWhen(Retry.fixedDelay(1, Duration.ofSeconds(300)))
//                .doOnError(IOException.class, e -> LOG.error("Round" + round, e))
                .doOnError(Exception.class, e -> LOG.error("Round" + round, e));

        this.keeper = keeper;
        flux.subscribe(s -> this.keeper.getChatBroadcasters(round).forEach(chatBroadcaster -> chatBroadcaster.addPartOfPgn(s)));
    }

}
