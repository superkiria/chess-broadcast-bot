package com.github.superkiria.cbbot.sending;

import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.sending.keepers.SentMessageKeeper;
import com.github.superkiria.cbbot.sending.model.GameKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SentMessageProcessor {

    private final SentMessageKeeper keeper;

    @Autowired
    public SentMessageProcessor(SentMessageKeeper keeper) {
        this.keeper = keeper;
    }

    void process(ChatContext context, Message message) {
        if (message == null || context.getInputStream() == null) {
            return;
        }
        GameKey key = GameKey.builder().round(context.getRound()).white(context.getWhite()).black(context.getBlack()).build();
        context.setMessageId(message.getMessageId());
        keeper.putGame(key, context);
    }

}
