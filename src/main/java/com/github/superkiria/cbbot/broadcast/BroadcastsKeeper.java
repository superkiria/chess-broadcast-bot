package com.github.superkiria.cbbot.broadcast;

import com.github.superkiria.cbbot.queue.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BroadcastsKeeper {

    private final MessageQueue messageQueue;
    private final Map<String, List<ChatBroadcaster>> broadcasters = new ConcurrentHashMap<>();

    @Autowired
    public BroadcastsKeeper(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void registerBroadcast(String chatId, String round) {
        ChatBroadcaster chatBroadcaster = new ChatBroadcaster(messageQueue, chatId, round);
        addBroadcaster(round, chatBroadcaster);
    }

    private void addBroadcaster(String round, ChatBroadcaster broadcaster) {
        broadcasters.computeIfAbsent(round, k -> new ArrayList<>());
        synchronized (broadcasters.get(round)) {
            broadcasters.get(round).add(broadcaster);
        }
    }

    public List<ChatBroadcaster> getChatBroadcasters(String round) {
        return broadcasters.getOrDefault(round, new ArrayList<>());
    }

}
