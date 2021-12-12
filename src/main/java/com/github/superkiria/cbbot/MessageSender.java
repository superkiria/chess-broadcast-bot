package com.github.superkiria.cbbot;

import com.github.superkiria.cbbot.queue.MessageQueue;
import com.github.superkiria.cbbot.queue.MessageQueueObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        for (;;) {
            MessageQueueObject messageObject = queue.poll();
            try {
                long now = new Date().getTime();
                if (now - last.getTime() <  34) {
                    Thread.sleep(34 - now + last.getTime());
                    now = new Date().getTime();
                }
                if (lastForUser.get(messageObject.getChatId()) != null
                        && now - lastForUser.get(messageObject.getChatId()).getTime() <  1000) {
                    Thread.sleep(1000 - now + lastForUser.get(messageObject.getChatId()).getTime());
                }
                last = new Date();
                lastForUser.put(messageObject.getChatId(), last);
                if (messageObject.getContext().getResponse() != null) {
                    bot.execute(messageObject.getContext().getResponse()); // Call method to send the message
                }
//                if (messageObject.getContext().getSendPhoto() != null) {
//                    bot.execute(messageObject.getContext().getSendPhoto()); // Call method to send the message
//                }
            } catch (TelegramApiException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
