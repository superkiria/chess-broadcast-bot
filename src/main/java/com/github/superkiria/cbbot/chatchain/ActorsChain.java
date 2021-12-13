package com.github.superkiria.cbbot.chatchain;

import com.github.superkiria.cbbot.chatchain.actors.ButtonClickActor;
import com.github.superkiria.cbbot.chatchain.actors.ChatIdExtractor;
import com.github.superkiria.cbbot.chatchain.actors.MenuActor;
import com.github.superkiria.cbbot.chatchain.actors.SendMessageActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ActorsChain {

    private final List<ChatActor> actors = new ArrayList<>();

    @Autowired
    public ActorsChain(ApplicationContext context) {
        actors.add(context.getBean(ChatIdExtractor.class));
        actors.add(context.getBean(MenuActor.class));
        actors.add(context.getBean(ButtonClickActor.class));
        actors.add(context.getBean(SendMessageActor.class));
    }

    public void startWithContext(ChatContext context) {
        Thread thread = new Thread(() -> {
            for (ChatActor actor : actors) {
                actor.act(context);
            }
        });
        thread.start();
    }

}
