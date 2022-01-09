package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.admin.SubscriptionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CancelSubscriptionActor implements ChatActor {

    @Autowired
    private final SubscriptionManager subscriptionManager;

    public CancelSubscriptionActor(SubscriptionManager subscriptionManager) {
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate().getMessage() == null) {
            return;
        }
        if (!context.getUpdate().getMessage().getText().strip().equals("cancel")) {
            return;
        }
        subscriptionManager.cancelSubscription();
    }
}
