package com.github.superkiria.chatchain.actors;

import com.github.superkiria.cbbot.queue.MessageQueueObject;
import com.github.superkiria.chatchain.ChatActor;
import com.github.superkiria.chatchain.ChatContext;
import org.springframework.stereotype.Component;

@Component
public class SendMessageActor extends AbstractBotActor implements ChatActor {

    @Override
    public void act(ChatContext context) {
        MessageQueueObject build = MessageQueueObject.builder()
                .chatId(context.getChatId())
                .context(context)
                .build();
        messageQueue.add(build);
    }

}
