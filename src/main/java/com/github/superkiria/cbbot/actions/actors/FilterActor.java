package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.main.ChatContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FilterActor implements ChatActor {

    @Value("${telegram.admin.chatId}")
    private String adminChatId;

    @Override
    public void act(ChatContext context) {
        if (!adminChatId.equals(context.getChatId())) {
            context.setSkip(true);
        }
    }

}
