package com.github.superkiria.cbbot.sending;

import com.github.superkiria.cbbot.main.ChatContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageSender {

    private final static Logger LOG = LoggerFactory.getLogger(MessageSender.class);

    private final MessageQueue queue;
    private final TelegramLongPollingBot bot;
    private final SentMessageProcessor processor;
    private final SentDataKeeper keeper;

    private Date last = new Date();
    private final Map<String, Date> lastForUser = new HashMap<>();

    @Autowired
    public MessageSender(MessageQueue queue, TelegramLongPollingBot bot, SentMessageProcessor processor, SentDataKeeper keeper) {
        this.queue = queue;
        this.bot = bot;
        this.processor = processor;
        this.keeper = keeper;
    }

    @EventListener
    public void start(ApplicationReadyEvent event) {
        new Thread(() -> {
            while (true) {
                try {
                    waitForBestElementInQueueWithPeek();
                    ChatContext context = queue.take();
                    context.setReplyMessageId(keeper.getMessageId(context.getKey()));
                    waitToNotViolateTelegramRestrictions(context.getChatId());
                    Message message = context.call(bot);
                    processor.process(context, message);
                    LOG.debug("Message sent. chatId {}, messageId {}, gameKey: {}",
                            context.getChatId(),
                            message != null ? message.getMessageId() : null,
                            context.getKey());
                    LOG.trace("Message sent. context: {}",
                            context);
                } catch (TelegramApiException | InterruptedException e) {
                    LOG.error("Message processing error", e);
                }
            }
        }).start();
    }

    private void waitForBestElementInQueueWithPeek() throws InterruptedException {
        ChatContext peeked = queue.peek();
        if (!queue.isEmpty()) {
            while (lastForUser.containsKey(peeked.getChatId())
                    && new Date().getTime() - lastForUser.get(peeked.getChatId()).getTime() < 2950) {
                Thread.sleep(17);
                peeked = queue.peek();
            }
        }
    }

    private void waitToNotViolateTelegramRestrictions(String chatId) throws InterruptedException {
        long now = new Date().getTime();
        if (now - last.getTime() < 34) {
            Thread.sleep(34 - now + last.getTime());
            now = new Date().getTime();
        }
        if (lastForUser.get(chatId) != null
                && now - lastForUser.get(chatId).getTime() < 3000) {
            Thread.sleep(3000 - now + lastForUser.get(chatId).getTime());
        }
        last = new Date();
        lastForUser.put(chatId, last);
    }

}
