package com.github.superkiria.cbbot.chatchain.actors;

import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.chatchain.ChatActor;
import com.github.superkiria.cbbot.incoming.lichess.LichessConsumer;
import com.github.superkiria.cbbot.incoming.lichess.SubscriptionManager;
import com.github.superkiria.cbbot.incoming.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.incoming.lichess.model.LichessRound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.stream.Stream;

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
                Stream<LichessRound> sorted = first.get().getRounds().stream().sorted(Comparator.comparing(LichessRound::getStartsAt));
                List<String> texts = new ArrayList<>();
                texts.add(first.get().getTour().getName());
                sorted.forEach(round -> texts.add(round.getName()
                        + " | " + round.getStartsAt()
                        + " | " + (round.getOngoing() != null && round.getOngoing() ? "ongoing" : "")
                        +  (round.getFinished() != null && round.getFinished() ? "finished" : "")));
                context.setResponse(String.join("\n", texts));

                InlineKeyboardMarkup.InlineKeyboardMarkupBuilder markup = InlineKeyboardMarkup.builder();
                InlineKeyboardButton button = InlineKeyboardButton.builder()
                        .callbackData("subscribe:" + call.substring(5))
                        .text("Подписаться на этот турнир")
                        .build();
                markup.keyboardRow(Collections.singletonList(button));

                context.setInlineKeyboardMarkup(markup.build());
            }
        } else if (call.startsWith("subscribe:")) {
            subscriptionManager.subscribeForEvent(call.substring(10));
            context.setResponse("Subscribe call accepted");
        }
    }
}
