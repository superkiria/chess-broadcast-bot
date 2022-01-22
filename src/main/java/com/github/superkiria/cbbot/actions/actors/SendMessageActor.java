package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.sending.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendMessageActor implements ChatActor {

    private final MessageQueue messageQueue;

    @Autowired
    public SendMessageActor(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void act(ChatContext context) {
        messageQueue.add(context);
    }

}
