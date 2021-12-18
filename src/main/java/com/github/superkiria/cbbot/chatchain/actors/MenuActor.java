package com.github.superkiria.cbbot.chatchain.actors;

import com.github.superkiria.cbbot.chatchain.ChatActor;
import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.incoming.lichess.LichessConsumer;
import com.github.superkiria.cbbot.incoming.lichess.model.LichessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MenuActor implements ChatActor {

    private final LichessConsumer broadcastConsumer;

    @Autowired
    public MenuActor(LichessConsumer broadcastConsumer) {
        this.broadcastConsumer = broadcastConsumer;
    }

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate().getMessage() == null) {
            return;
        }
        List<LichessEvent> lichessBroascasts = broadcastConsumer.getLichessBroadcasts();
        List<LichessEvent> ongoingTours = lichessBroascasts.stream().filter(
                o -> o.getRounds().stream().anyMatch(r -> r.getFinished() == null)
        ).collect(Collectors.toList());

        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder markup = InlineKeyboardMarkup.builder();

        for(LichessEvent event : ongoingTours) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .callbackData(event.getRounds().stream().filter(r -> r.getFinished() == null).findFirst().get().getId())
                    .text(event.getTour().getName()
                            + " - " + event.getRounds().stream().filter(r -> r.getFinished() == null).findFirst().get().getName())
                    .build();
            markup.keyboardRow(Collections.singletonList(button));
        }

        context.setInlineKeyboardMarkup(markup.build());
        context.setResponse("Список турниров:");
    }

}
