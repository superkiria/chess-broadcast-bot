package com.github.superkiria.cbbot.processing;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class PgnQueue {

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public void putPgnPart(String s) {
        queue.add(s);
    }

    public String take() throws InterruptedException {
        return queue.take();
    }
}
