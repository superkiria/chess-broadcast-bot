package com.github.superkiria.cbbot.outgoing;

import com.github.superkiria.cbbot.chatchain.ChatContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class MessageQueue {

    private final List<Queue<ChatContext>> partitions = new ArrayList<>();

    private int currentPartition = 0;

    public MessageQueue() {
        for (int i = 0; i < 90; i++) {
            partitions.add(new ConcurrentLinkedDeque<>());
        }
    }

    public boolean add(ChatContext o) {
        return partitions.get(Math.abs(o.getChatId().hashCode() % partitions.size())).add(o);
    }

    public ChatContext poll() {
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
