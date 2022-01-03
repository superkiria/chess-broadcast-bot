package com.github.superkiria.cbbot.outgoing.keepers;

import com.github.superkiria.cbbot.outgoing.model.GameKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class GameCountKeeper {

    private final static Logger LOG = LoggerFactory.getLogger(GameCountKeeper.class);
    private Set<String> games = new HashSet<>();

    public synchronized void add(GameKey key) {
        LOG.info("Adding game {}", key);
        games.add(key.toString());
    }

    public synchronized int getCount() {
        LOG.info("Current games {}", games);
        return games.size();
    }

    public synchronized void clear() {
        LOG.info("Clear game count");
        games = new HashSet<>();
    }

}
