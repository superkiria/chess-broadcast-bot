package com.github.superkiria.lichess.stub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RequestMapping("/api/stream/broadcast/round")
@RestController
public class LichessBroadcastStub {

    @Autowired
    private StreamFileReader streamFileReader;

    @GetMapping(value = "/qwerty.png", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Object> streamDataFlux() {
        return Flux.interval(Duration.ofMillis(500)).map(i -> streamFileReader.readLine(i.intValue()));
    }

}
