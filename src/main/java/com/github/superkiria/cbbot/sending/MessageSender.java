package com.github.superkiria.cbbot.sending;

import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.sending.keepers.SentDataKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageSender {

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

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    if (!queue.isEmpty() && lastForUser.containsKey(queue.peek().getChatId())) {
                        while (new Date().getTime() - lastForUser.get(queue.peek().getChatId()).getTime() < 2750) {
                            Thread.sleep(200);
                        }
                    }
                    ChatContext context = queue.take();
                    Integer messageId = keeper.getMessageId(context.getKey());
                    context.setMessageId(messageId);
                    long now = new Date().getTime();
                    if (now - last.getTime() < 34) {
                        Thread.sleep(34 - now + last.getTime());
                        now = new Date().getTime();
                    }
                    if (lastForUser.get(context.getChatId()) != null
                            && now - lastForUser.get(context.getChatId()).getTime() < 3000) {
                        Thread.sleep(3000 - now + lastForUser.get(context.getChatId()).getTime());
                    }
                    last = new Date();
                    lastForUser.put(context.getChatId(), last);
                    Message message = context.call(bot);
                    processor.process(context, message);
                } catch (TelegramApiException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
