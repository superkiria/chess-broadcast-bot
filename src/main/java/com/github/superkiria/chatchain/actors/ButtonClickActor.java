package com.github.superkiria.chatchain.actors;

import com.github.superkiria.cbbot.broadcast.BroadcastsKeeper;
import com.github.superkiria.chatchain.ChatActor;
import com.github.superkiria.chatchain.ChatContext;
import com.github.superkiria.lichess.LichessConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class ButtonClickActor implements ChatActor {

    private final BroadcastsKeeper broadcastsKeeper;
    private final LichessConsumer broadcastConsumersKeeper;

    @Autowired
    public ButtonClickActor(BroadcastsKeeper broadcastsKeeper, LichessConsumer lichessConsumer) {
        this.broadcastsKeeper = broadcastsKeeper;
        this.broadcastConsumersKeeper = lichessConsumer;
    }

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate().getCallbackQuery() == null) {
            return;
        }
        String round = context.getUpdate().getCallbackQuery().getData().trim();
        broadcastConsumersKeeper.subscribeForRound(round);
        broadcastsKeeper.registerBroadcast(context.getChatId(), context.getUpdate().getCallbackQuery().getData());
        SendMessage message = SendMessage.builder()
                .text("Вы подписаны на игру!")
                .chatId(context.getChatId())
                .build();
        context.setResponse(message);

    }
}
