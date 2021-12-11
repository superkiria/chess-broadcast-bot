package com.github.superkiria.chatchain.actors;

import com.github.superkiria.cbbot.queue.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public abstract class AbstractBotActor {

    protected MessageQueue messageQueue;

    @Autowired
    public final void setBot(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

}
