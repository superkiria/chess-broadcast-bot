package com.github.superkiria.cbbot.sending.keepers;

import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.sending.model.GameKey;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SentMessageKeeper {

    private final Map<String, ChatContext> sentMessagesForGames = new ConcurrentHashMap<>();

    public ChatContext getGame(GameKey gameKey) {
        if (gameKey == null) {
            return null;
        }
        return sentMessagesForGames.get(gameKey.toString());
    }

    public void putGame(GameKey gameKey, ChatContext context) {
        sentMessagesForGames.put(gameKey.toString(), context);
    }


    public void clear() {
        sentMessagesForGames.clear();
    }

    public int getCount() {
        return sentMessagesForGames.size();
    }

    public void putGameIfAbsent(GameKey key, ChatContext context) {
        if (sentMessagesForGames.containsKey(key)) {
            return;
        }
        sentMessagesForGames.put(key.toString(), context);
    }
}
