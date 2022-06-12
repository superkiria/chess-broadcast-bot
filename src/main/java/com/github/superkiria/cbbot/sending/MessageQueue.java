package com.github.superkiria.cbbot.sending;

import com.github.superkiria.cbbot.main.ChatContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class MessageQueue {

    private final Map<String, Long> scheduledShipments = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<ChatContext> queue = new PriorityBlockingQueue<>();

    public void add(ChatContext o) {
        if (o == null) {
            return;
        }
        Long scheduledTime = Math.max(new Date().getTime(), scheduledShipments.getOrDefault(o.getChatId(), 0L) + 3000);
        scheduledShipments.put(o.getChatId(), scheduledTime);
        o.setScheduledTime(scheduledTime);
        queue.add(o);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public ChatContext peek() {
        return queue.peek();
    }

    public ChatContext take() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }
}
