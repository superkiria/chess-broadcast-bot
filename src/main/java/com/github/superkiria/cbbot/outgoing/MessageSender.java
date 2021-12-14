package com.github.superkiria.cbbot.outgoing;

import com.github.superkiria.cbbot.chatchain.ChatContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageSender {

    private final MessageQueue queue;
    private final TelegramLongPollingBot bot;
    private Date last = new Date();
    private final Map<String, Date> lastForUser = new HashMap<>();

    public MessageSender(MessageQueue queue, TelegramLongPollingBot bot) {
        this.queue = queue;
        this.bot = bot;
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                ChatContext context = queue.poll();
                try {
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
                    if (context.getResponse() != null) {
                        bot.execute(context.getResponse()); // Call method to send the message
                    }
                    if (context.getSendPhoto() != null) {
                        bot.execute(context.getSendPhoto()); // Call method to send the message
                    }
                } catch (TelegramApiException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
