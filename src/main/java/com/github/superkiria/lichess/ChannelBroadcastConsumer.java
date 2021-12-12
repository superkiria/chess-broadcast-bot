package com.github.superkiria.lichess;

import com.github.superkiria.lichess.model.LichessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;

@Component
public class ChannelBroadcastConsumer {

    private final WebClient webClient;
    private final static Logger LOG = LoggerFactory.getLogger(ChannelBroadcastConsumer.class);

    @Autowired
    public ChannelBroadcastConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<String> pgnsForRound(String round) {
        return webClient.get()
                .uri("https://lichess.org/api/stream/broadcast/round/{round}.pgn", round)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnSubscribe(o -> LOG.info("Subscibed for lichess broadcast: {}", o.toString()))
                .retryWhen(Retry.fixedDelay(10, Duration.ofSeconds(10)))
                .doOnError(IOException.class, e -> LOG.error("Round" + round, e));
    }

}