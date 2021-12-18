package com.github.superkiria.cbbot.outgoing;

import com.github.superkiria.cbbot.chatchain.ChatContext;
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
    private Date last = new Date();
    private final Map<String, Date> lastForUser = new HashMap<>();

    @Autowired
    public MessageSender(MessageQueue queue, TelegramLongPollingBot bot, SentMessageProcessor processor) {
        this.queue = queue;
        this.bot = bot;
        this.processor = processor;
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    ChatContext context = queue.take();
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
