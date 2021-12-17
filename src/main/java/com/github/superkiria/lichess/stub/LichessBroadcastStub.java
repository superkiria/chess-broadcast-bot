package com.github.superkiria.lichess.stub;

import com.github.superkiria.cbbot.chatchain.actors.ButtonClickActor;
import com.github.superkiria.cbbot.incoming.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.incoming.lichess.model.LichessRound;
import com.github.superkiria.cbbot.incoming.lichess.model.LichessTour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RequestMapping("/api")
@RestController
public class LichessBroadcastStub {

    private final static Logger LOG = LoggerFactory.getLogger(LichessBroadcastStub.class);

    @Autowired
    private StreamFileReader streamFileReader;

    @GetMapping(value = "/stream/broadcast/round/qwerty.pgn", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<String> streamDataFlux() {
        LOG.info("Get /stream/broadcast/round/qwerty.pgn");
        return Flux.interval(Duration.ofMillis(200)).map(i -> streamFileReader.readLine(i.intValue()) + "\n");
    }

    @GetMapping(value = "/broadcast", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public LichessEvent timetable() {
        return LichessEvent.builder()
                .tour(LichessTour.builder().id("tourId").name("tourName").description("").markup("").slug("").url("").build())
                .rounds(Collections.singletonList(LichessRound.builder().ongoing(true).name("test LichessRound").id("qwerty").finished(null).slug("").startsAt(null).url("").build())).build();
    }

}
