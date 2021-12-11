package com.github.superkiria.chatchain.actors;

import com.github.superkiria.chatchain.ChatActor;
import com.github.superkiria.chatchain.ChatContext;
import org.springframework.stereotype.Component;

@Component
public class ChatIdExtractor implements ChatActor {

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate().getMessage() != null) {
            context.setChatId(context.getUpdate().getMessage().getChat().getId().toString());
        }
        if (context.getUpdate().getCallbackQuery() != null) {
            context.setChatId(context.getUpdate().getCallbackQuery().getMessage().getChatId().toString());
        }
    }

}
