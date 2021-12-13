package com.github.superkiria.cbbot.chatchain.actors;

import com.github.superkiria.cbbot.broadcast.PgnDispatcher;
import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.chatchain.ChatActor;
import com.github.superkiria.cbbot.incoming.lichess.LichessConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class ButtonClickActor implements ChatActor {

    private final PgnDispatcher pgnDispatcher;
    private final LichessConsumer broadcastConsumersKeeper;

    @Autowired
    public ButtonClickActor(PgnDispatcher pgnDispatcher, LichessConsumer lichessConsumer) {
        this.pgnDispatcher = pgnDispatcher;
        this.broadcastConsumersKeeper = lichessConsumer;
    }

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate().getCallbackQuery() == null) {
            return;
        }
        String round = context.getUpdate().getCallbackQuery().getData().trim();
        broadcastConsumersKeeper.subscribeForRound(round);
//        pgnDispatcher.registerBroadcast(context.getChatId(), context.getUpdate().getCallbackQuery().getData());
        SendMessage message = SendMessage.builder()
                .text("Вы подписаны на игру!")
                .chatId(context.getChatId())
                .build();
        context.setResponse(message);

    }
}
