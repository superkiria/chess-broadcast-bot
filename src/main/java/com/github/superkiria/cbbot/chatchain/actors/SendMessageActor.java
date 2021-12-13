package com.github.superkiria.cbbot.chatchain.actors;

import com.github.superkiria.cbbot.chatchain.ChatActor;
import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.queue.MessageQueueObject;
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
