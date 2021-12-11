package com.github.superkiria.cbbot.queue;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class MessageQueue {

    private final List<Queue<MessageQueueObject>> partitions = new ArrayList<>();

    private int currentPartition = 0;

    public MessageQueue() {
        for (int i = 0; i < 30; i++) {
            partitions.add(new ConcurrentLinkedDeque<>());
        }
    }

    public int getPartitionsAmount() {
        return partitions.size();
    }

    public boolean add(MessageQueueObject o) {
        return partitions.get(Math.abs(o.getChatId().hashCode() % partitions.size())).add(o);
    }

    public MessageQueueObject poll() {
        synchronized (this) {
            while (partitions.get(currentPartition).isEmpty()) {
                currentPartition++;
                if (currentPartition >= partitions.size()) {
                    currentPartition = 0;
                }
            }
            return partitions.get(currentPartition).poll();
        }
    }

}
