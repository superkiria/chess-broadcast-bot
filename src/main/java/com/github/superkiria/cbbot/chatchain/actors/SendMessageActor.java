package com.github.superkiria.cbbot.chatchain.actors;

import com.github.superkiria.cbbot.chatchain.ChatActor;
import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.outgoing.MessageQueue;
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
        messageQueue.addHighPriority(context);
    }

}
