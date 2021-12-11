package com.github.superkiria.lichess;

import com.github.superkiria.cbbot.broadcast.BroadcastsKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BroadcastConsumersKeeper {

    private final BroadcastsKeeper keeper;
    private final Map<String, BroadcastConsumer> broadcastConsumers = new ConcurrentHashMap<>();

    @Autowired
    public BroadcastConsumersKeeper(BroadcastsKeeper keeper) {
        this.keeper = keeper;
    }

    public BroadcastConsumer registerBroadcast(String round) {
        synchronized (broadcastConsumers) {
            if (broadcastConsumers.get(round) == null) {
                broadcastConsumers.put(round, new BroadcastConsumer(round, keeper));
            }
        }
        return broadcastConsumers.get(round);
    }

}
