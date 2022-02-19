package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.main.ChatContext;
import org.springframework.stereotype.Component;

@Component
public class ChatIdExtractor implements ChatActor {

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate().getMessage() != null) {
            context.setChatId(context.getUpdate().getMessage().getChat().getId().toString());
        } else if (context.getUpdate().getCallbackQuery() != null) {
            context.setChatId(context.getUpdate().getCallbackQuery().getMessage().getChatId().toString());
        }
    }

}
