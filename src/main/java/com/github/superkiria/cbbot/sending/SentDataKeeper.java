package com.github.superkiria.cbbot.sending;

import com.github.superkiria.cbbot.processing.model.GameMoveInfo;
import com.github.superkiria.cbbot.processing.model.GameKey;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SentDataKeeper {

    private final Map<String, Integer> messageIds  = new ConcurrentHashMap<>();
    private final Map<String, Integer> colors  = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> forwards = new ConcurrentHashMap<>();
    private final Map<String, String> openings = new ConcurrentHashMap<>();
    private final Map<String, GameMoveInfo> lastValidGame = new ConcurrentHashMap<>();

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

    public String getOpening(GameKey gameKey) {
        return openings.get(gameKey.toString());
    }

    public void putOpening(GameKey gameKey, String opening) {
        openings.put(gameKey.toString(), opening);
    }

    public GameMoveInfo getLastValidGame(GameKey key) {
        return lastValidGame.get(key.toString());
    }

    public void putLastValidGame(GameKey key, GameMoveInfo game) {
        lastValidGame.put(key.toString(), game);
    }

    public void clear() {
        messageIds.clear();
        colors.clear();
        forwards.clear();
        openings.clear();
        lastValidGame.clear();
    }

    public int getColorsCount() {
        return colors.size();
    }

    public int getMessageIdsCount() {
        return messageIds.size();
    }

}
