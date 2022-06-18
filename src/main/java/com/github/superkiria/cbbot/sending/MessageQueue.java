package com.github.superkiria.cbbot.sending;

import com.github.superkiria.cbbot.main.ChatContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class MessageQueue {

    private final Map<String, Long> scheduledShipmentsByChat = new ConcurrentHashMap<>();
    private final Map<String, Long> scheduledShipmentsByGame = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<ChatContext> queue = new PriorityBlockingQueue<>();

    public synchronized void add(ChatContext o) {
        if (o == null) {
            return;
        }
        Long scheduledTime = 3000 + Math.max(new Date().getTime() - 3000,
                                 Math.max(scheduledShipmentsByGame.getOrDefault(o.getChatId() + "_" + o.getKey(), 0L) + 1000, scheduledShipmentsByChat.getOrDefault(o.getChatId(), 0L)));
        if (o.getKey() != null) {
            scheduledShipmentsByGame.put(o.getChatId() + "_" + o.getKey(), scheduledTime);
            System.out.println(scheduledShipmentsByGame);
        } else {
            scheduledShipmentsByChat.put(o.getChatId(), scheduledTime);
        }
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

    private long max(long a, long b, long c) {
        return Math.max(Math.max(a, b), c);
    }
}
