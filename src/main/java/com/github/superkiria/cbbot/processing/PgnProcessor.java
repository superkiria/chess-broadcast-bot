package com.github.superkiria.cbbot.processing;

import com.github.bhlangonijr.chesslib.pgn.PgnProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

import static com.github.bhlangonijr.chesslib.pgn.PgnProperty.parsePgnProperty;

@Component
public class PgnProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(PgnProcessor.class);

    private final PgnQueue incoming;

    @Autowired
    public PgnProcessor(PgnQueue incoming) {
        this.incoming = incoming;
    }

    public List<String> waitAndPreparePgn() throws InterruptedException {
        LinkedList<String> buffer = new LinkedList<>();
        String part;
        boolean hasEventInfo = false;
        do {
            part = incoming.take().trim();
            if (PgnProperty.isProperty(part) && parsePgnProperty(part).name.equalsIgnoreCase("event")) {
                hasEventInfo = true;
            }
            if (PgnProperty.isProperty(part) && parsePgnProperty(part).name.equalsIgnoreCase("TimeControl")) {
                LOG.trace("Skipped: {}", part);
                continue;
            }
            buffer.add(part);
            LOG.trace(part);
        } while (!(part.endsWith("*") ||
                part.endsWith("0-1") ||
                part.endsWith("1/2-1/2") ||
                part.endsWith("1-0")));
        if (!hasEventInfo) {
            buffer.addFirst("[Event \"Fake event 2022\"]");
        }
        return buffer;
    }

}
