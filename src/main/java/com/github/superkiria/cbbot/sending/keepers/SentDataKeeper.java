package com.github.superkiria.cbbot.sending.keepers;

import com.github.superkiria.cbbot.sending.model.GameKey;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SentDataKeeper {

    private final Map<String, Integer> messageIds  = new ConcurrentHashMap<>();
    private final Map<String, Integer> colors  = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> forwards = new ConcurrentHashMap<>();

    public Integer getMessageId(GameKey gameKey) {
        if (gameKey == null) {
            return null;
        }
        return messageIds.get(gameKey.toString());
    }

    public void putMessageId(GameKey gameKey, Integer messageId) {
        messageIds.put(gameKey.toString(), messageId);
    }

    public Integer getColor(GameKey gameKey) {
        if (gameKey == null) {
            return null;
        }
        return colors.get(gameKey.toString());
    }

    public void putColor(GameKey gameKey, int color) {
        colors.put(gameKey.toString(), color);
    }

    public Integer getForward(Integer from) {
        return forwards.get(from);
    }

    public void putForward(Integer from, Integer to) {
        forwards.put(from, to);
    }

    public void clear() {
        messageIds.clear();
        colors.clear();
    }

    public int getColorsCount() {
        return colors.size();
    }

    public int getMessageIdsCount() {
        return messageIds.size();
    }

}
