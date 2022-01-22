package com.github.superkiria.cbbot.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class PgnQueue {

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private static final Logger LOG = LoggerFactory.getLogger("pgndump");

    public void putPgnPart(String s) {
        queue.add(s);
        LOG.trace(s);
    }

    public String take() throws InterruptedException {
        return queue.take();
    }
}
