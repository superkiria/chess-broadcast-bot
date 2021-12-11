package com.github.superkiria.chatchain.actors;

import com.github.superkiria.chatchain.ChatActor;
import com.github.superkiria.chatchain.ChatContext;
import com.github.superkiria.lichess.ChannelBroadcastConsumer;
import com.github.superkiria.lichess.model.LichessEvent;
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

    private final ChannelBroadcastConsumer broadcastConsumer;

    @Autowired
    public MenuActor(ChannelBroadcastConsumer broadcastConsumer) {
        this.broadcastConsumer = broadcastConsumer;
    }

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate().getMessage() == null) {
            return;
        }
        List<LichessEvent> lichessBroascasts = broadcastConsumer.getLichessBroadcasts();
        List<LichessEvent> ongoingTours = lichessBroascasts.stream().filter(
                o -> o.getRounds().stream().anyMatch(r -> r.getOngoing() != null)
        ).collect(Collectors.toList());

        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder markup = InlineKeyboardMarkup.builder();

        for(LichessEvent event : ongoingTours) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .callbackData(event.getRounds().stream().filter(r -> r.getOngoing() != null).findFirst().get().getId())
                    .text(event.getTour().getName()
                            + " - " + event.getRounds().stream().filter(r -> r.getOngoing() != null).findFirst().get().getName())
                    .build();
            markup.keyboardRow(Collections.singletonList(button));
        }

        SendMessage message = SendMessage.builder()
                .text("Список турниров:")
                .replyMarkup(markup.build())
                .chatId(context.getChatId())
                .build();
        context.setResponse(message);
    }

}
