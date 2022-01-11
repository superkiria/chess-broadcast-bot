package com.github.superkiria.cbbot.sending;

import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.processing.PgnDispatcher;
import com.github.superkiria.cbbot.sending.keepers.SentMessageKeeper;
import com.github.superkiria.cbbot.sending.model.GameKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SentMessageProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(PgnDispatcher.class);

    private final SentMessageKeeper keeper;

    @Autowired
    public SentMessageProcessor(SentMessageKeeper keeper) {
        this.keeper = keeper;
    }

    void process(ChatContext context, Message message) {
        if (message == null || context.getInputStream() == null || message.getMessageId() == null) {
            return;
        }
        context.setMessageId(message.getMessageId());
        keeper.putGame(context.getKey(), context);
        LOG.info("SentMessageKeeper: chatId {} for game {}, total {} games", message.getMessageId(), context.getKey(), keeper.getCount());
    }

}
