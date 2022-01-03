package com.github.superkiria.cbbot.chatchain.actors;

import com.github.superkiria.cbbot.chatchain.ChatActor;
import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.incoming.lichess.LichessConsumer;
import com.github.superkiria.cbbot.incoming.lichess.model.LichessEvent;
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
        if (context.getUpdate().getMessage() == null) {
            return;
        }
        if (!context.getUpdate().getMessage().getText().strip().equals("tt")) {
            return;
        }
        List<LichessEvent> lichessBroascasts = broadcastConsumer.getLichessBroadcasts();
        List<LichessEvent> ongoingTours = lichessBroascasts.stream().filter(
                o -> o.getRounds().stream().anyMatch(r -> r.getFinished() == null)
        ).collect(Collectors.toList());
//        List<LichessEvent> ongoingTours = lichessBroascasts;

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
        context.setResponse("Found " + ongoingTours.size() + " tours:\n" + ongoingTours.stream().map(t -> t.getTour().getName() + "\n" + t.getTour().getDescription() + "\n" + t.getTour().getUrl()).collect(Collectors.joining("\n\n")));
    }

}
