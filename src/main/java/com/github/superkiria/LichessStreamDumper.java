package com.github.superkiria;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.util.retry.Retry;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

@Component
public class LichessStreamDumper {

    private final WebClient webClient;

    public LichessStreamDumper(WebClient webClient) {
        this.webClient = webClient;
    }

    void start(String round) {
        Disposable subscription = webClient.get()
                .uri("https://lichess.org/api/stream/broadcast/round/{round}.pgn", round)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnSubscribe(o -> System.out.println("Subscibed for lichess broadcast"))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(60)))
                .doOnError(Exception.class, System.out::println)
                .subscribe(this::append);
    }

    void append(String text) {
        try(FileWriter writer = new FileWriter("stream.txt", true))
        {
            writer.append(text);
            writer.append('\n');
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

}
