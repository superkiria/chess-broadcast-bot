package com.github.superkiria.cbbot.outgoing;

import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.outgoing.model.GameKey;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SentMessageKeeper {

    Map<GameKey, ChatContext> sentMessagesForGames = new ConcurrentHashMap<>();

    ChatContext getGame(GameKey gameKey) {
        return sentMessagesForGames.get(gameKey);
    }

    void putGame(GameKey gameKey, ChatContext context) {
        sentMessagesForGames.put(gameKey, context);
    }

}
