package com.github.superkiria.cbbot.actions;

import com.github.superkiria.cbbot.actions.actors.*;
import com.github.superkiria.cbbot.main.ChatContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ActorsChain {

    private final ExecutorService pool = Executors.newFixedThreadPool(5);
    private final List<ChatActor> actors = new ArrayList<>();

    @Autowired
    public ActorsChain(ApplicationContext context) {
        actors.add(context.getBean(LogMessageActor.class));
        actors.add(context.getBean(ChatIdExtractor.class));
        actors.add(context.getBean(CatchForwardsActor.class));
        actors.add(context.getBean(FilterActor.class));
        actors.add(context.getBean(CancelSubscriptionActor.class));
        actors.add(context.getBean(TourTableMenuActor.class));
        actors.add(context.getBean(StatusActor.class));
        actors.add(context.getBean(ButtonClickActor.class));
        actors.add(context.getBean(SendMessageActor.class));
    }

    public void startWithContext(ChatContext context) {
        Runnable task = () -> {
            for (ChatActor actor : actors) {
                if (!context.isSkip()) {
                    actor.act(context);
                }
            }
        };
        pool.submit(task);
    }

}
