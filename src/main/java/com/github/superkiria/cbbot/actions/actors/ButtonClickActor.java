package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.lichess.LichessConsumer;
import com.github.superkiria.cbbot.admin.SubscriptionManager;
import com.github.superkiria.cbbot.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.sending.model.MarkedCaption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.github.superkiria.cbbot.processing.ComposeMessageHelper.eventInfo;
import static com.github.superkiria.cbbot.processing.ComposeMessageHelper.eventSubscribeButton;

@Component
public class ButtonClickActor implements ChatActor {

    private final static Logger LOG = LoggerFactory.getLogger(ButtonClickActor.class);

    private final LichessConsumer lichess;
    private final SubscriptionManager subscriptionManager;

    @Autowired
    public ButtonClickActor(LichessConsumer lichess, SubscriptionManager subscriptionManager) {
        this.lichess = lichess;
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate().getCallbackQuery() == null) {
            return;
        }
        String call = context.getUpdate().getCallbackQuery().getData().trim();
        if (call.startsWith("tour:")) {
            Optional<LichessEvent> first = lichess.getLichessBroadcasts().stream().filter(t -> t.getTour().getId().equals(call.substring(5))).findFirst();
            if (first.isPresent()) {
                context.setMarkedCaption(MarkedCaption.builder().caption(eventInfo(first.get())).build());
                context.setInlineKeyboardMarkup(eventSubscribeButton(call.substring(5)));
            }
        } else if (call.startsWith("subscribe:")) {
            subscriptionManager.subscribeForEvent(call.substring(10));
            context.setMarkedCaption(MarkedCaption.builder().caption("Subscribe call accepted").build());
        }
    }
}
