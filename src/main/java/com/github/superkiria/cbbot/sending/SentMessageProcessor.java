package com.github.superkiria.cbbot.sending;

import com.github.superkiria.cbbot.main.ChatContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SentMessageProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(SentMessageProcessor.class);

    private final SentDataKeeper keeper;
    private final MessageQueue queue;

    @Value("${telegram.group.chatId}")
    private String groupId;

    @Autowired
    public SentMessageProcessor(SentDataKeeper keeper, MessageQueue queue) {
        this.keeper = keeper;
        this.queue = queue;
    }

    void process(ChatContext context, Message message) {
        if (context.getKey() == null || message == null) {
            return;
        }
        if (context.getFileId() == null && context.getReplyMessageId() != null && message.hasPhoto()) {
            context.setFileId(message.getPhoto().get(0).getFileId());
            context.setChatId(groupId);
            context.setForwardedReplyMessageId(keeper.getForward(context.getReplyMessageId()));
            queue.add(context);
        }
        if (context.getFileId() != null && context.getOpening() != null) {
            keeper.putOpening(context.getKey(), context.getOpening());
        }
        keeper.putMessageId(context.getKey(), message.getMessageId());
        LOG.debug("messageId {} for game {}, total {} games", message.getMessageId(), context.getKey(), keeper.getMessageIdsCount());
    }

}
