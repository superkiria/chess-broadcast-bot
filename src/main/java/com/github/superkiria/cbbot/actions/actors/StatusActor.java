package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.lichess.LichessConsumer;
import com.github.superkiria.cbbot.admin.SubscriptionManager;
import com.github.superkiria.cbbot.sending.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatusActor implements ChatActor {

    private final MessageQueue messageQueue;
    private final SubscriptionManager subscriptionManager;
    private final LichessConsumer lichessConsumer;

    @Autowired
    public StatusActor(MessageQueue messageQueue, SubscriptionManager subscriptionManager, LichessConsumer lichessConsumer) {
        this.messageQueue = messageQueue;
        this.subscriptionManager = subscriptionManager;
        this.lichessConsumer = lichessConsumer;
    }

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate().getMessage() == null) {
            return;
        }
        if (!context.getUpdate().getMessage().getText().strip().equalsIgnoreCase("st")) {
            return;
        }
        context.setResponse("Status:\n"
                + messageQueue.size() + "\n"
                + subscriptionManager.getCurrentSubscription() + "\n"
                + subscriptionManager.bestRoundToSubscribe() + "\n"
                + lichessConsumer.getCurrentSubscriptionRoundId() + "\n"
                + subscriptionManager.nextBestRoundToSubscribe());
    }

}
