package com.github.superkiria.cbbot.outgoing.keepers;

import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.outgoing.model.GameKey;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SentMessageKeeper {

    private final Map<GameKey, ChatContext> sentMessagesForGames = new ConcurrentHashMap<>();

    public ChatContext getGame(GameKey gameKey) {
        return sentMessagesForGames.get(gameKey);
    }

    public void putGame(GameKey gameKey, ChatContext context) {
        sentMessagesForGames.put(gameKey, context);
    }

}
