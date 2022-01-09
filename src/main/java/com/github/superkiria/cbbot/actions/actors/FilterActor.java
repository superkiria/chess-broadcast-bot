package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.props.TelegramProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterActor implements ChatActor {

    private final TelegramProps telegramProps;

    @Autowired
    public FilterActor(TelegramProps telegramProps) {
        this.telegramProps = telegramProps;
    }

    @Override
    public void act(ChatContext context) {
        if (!telegramProps.getAdminChatId().equals(context.getChatId())) {
            context.setSkip(true);
        }
    }

}
