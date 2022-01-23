package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.lichess.LichessConsumer;
import com.github.superkiria.cbbot.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.sending.model.MarkedCaption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TourTableMenuActor implements ChatActor {

    private final static Logger LOG = LoggerFactory.getLogger(ButtonClickActor.class);

    private final LichessConsumer broadcastConsumer;

    @Autowired
    public TourTableMenuActor(LichessConsumer broadcastConsumer) {
        this.broadcastConsumer = broadcastConsumer;
    }

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate().getMessage() == null || context.getUpdate().getMessage().getText() == null) {
            return;
        }
        if (!context.getUpdate().getMessage().getText().strip().equalsIgnoreCase("tt")) {
            return;
        }
        List<LichessEvent> ongoingTours = broadcastConsumer.getActualLichessBroadcasts();

        LOG.info("Gathered info about {} tours", ongoingTours.size());

        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder markup = InlineKeyboardMarkup.builder();

        for(LichessEvent event : ongoingTours) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .callbackData("tour:" + event.getTour().getId())
                    .text(event.getTour().getName())
                    .build();
            markup.keyboardRow(Collections.singletonList(button));
        }

        context.setInlineKeyboardMarkup(markup.build());
        context.setMarkedCaption(MarkedCaption.builder()
                .caption("Found " + ongoingTours.size()
                            + " tours:\n\n"
                            + ongoingTours.stream().map(t -> t.getTour().getName() + "\n" + t.getTour().getDescription() + "\n" + t.getTour().getUrl())
                        .collect(Collectors.joining("\n\n"))).build());
    }

}
