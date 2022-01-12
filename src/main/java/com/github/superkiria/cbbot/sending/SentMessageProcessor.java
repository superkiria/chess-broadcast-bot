package com.github.superkiria.cbbot.sending;

import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.processing.PgnDispatcher;
import com.github.superkiria.cbbot.sending.keepers.SentDataKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SentMessageProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(SentMessageProcessor.class);

    private final SentDataKeeper keeper;

    @Autowired
    public SentMessageProcessor(SentDataKeeper keeper) {
        this.keeper = keeper;
    }

    void process(ChatContext context, Message message) {
        if (context.getKey() == null || message == null) {
            return;
        }
        keeper.putMessageId(context.getKey(), message.getMessageId());
        LOG.info("chatId {} for game {}, total {} games", message.getMessageId(), context.getKey(), keeper.getMessageIdsCount());
    }

}
