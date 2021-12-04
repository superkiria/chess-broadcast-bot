package com.github.superkiria.lichess;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;

@Component
public class BroadcastConsumer {

    private final WebClient webClient = WebClient.create();

    public Flux<String> pgnsForRound(String round) {
        return webClient.get()
                .uri("https://lichess.org/api/stream/broadcast/round/{round}.pgn", round)
                .retrieve()
                .bodyToFlux(String.class)
                .retryWhen(Retry.fixedDelay(10, Duration.ofSeconds(10)))
                .doOnError(IOException.class, e -> System.out.println(e.getMessage()));
    }

}
