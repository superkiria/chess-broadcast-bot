package com.github.superkiria.cbbot.processing.message;

import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.model.MarkedCaption;
import com.github.superkiria.cbbot.processing.model.GameKey;
import com.github.superkiria.cbbot.processing.model.GameMoveInfo;

public class ChatContextHelper {

    public static ChatContext makeChatContext(String chatId, GameMoveInfo gameMoveInfo, MarkedCaption caption, MarkedCaption shortCaption, GameKey key) {
        return ChatContext.builder()
                .chatId(chatId)
                .key(GameKey.builder()
                        .round(gameMoveInfo.getRound())
                        .white(gameMoveInfo.getWhite())
                        .black(gameMoveInfo.getBlack())
                        .build())
                .markedCaption(
                        MarkedCaption.builder()
                                .caption(caption.getCaption())
                                .entities(caption.getEntities())
                                .build()
                )
                .shortMarkedCaption(shortCaption)
                .key(key)
                .opening(gameMoveInfo.getGame().getOpening())
                .build();
    }

}
