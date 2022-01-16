package com.github.superkiria.cbbot.sending;

import com.github.superkiria.cbbot.main.ChatContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class MessageQueue {

    private final BlockingDeque<ChatContext> deque = new LinkedBlockingDeque<>();

    public void add(ChatContext o) {
        if (o == null) {
            return;
        }
        deque.addLast(o);
    }

    public void addHighPriority(ChatContext o) {
        deque.addFirst(o);
    }

    public ChatContext take() throws InterruptedException {
        return deque.take();
    }

    public int size() {
        return deque.size();
    }

    public void clear() {
        deque.clear();
    }
}
