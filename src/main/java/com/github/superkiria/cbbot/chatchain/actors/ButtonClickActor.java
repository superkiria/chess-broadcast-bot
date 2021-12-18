package com.github.superkiria.cbbot.chatchain.actors;

import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.chatchain.ChatActor;
import com.github.superkiria.cbbot.incoming.lichess.LichessConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ButtonClickActor implements ChatActor {

    private final static Logger LOG = LoggerFactory.getLogger(ButtonClickActor.class);

    private final LichessConsumer broadcastConsumersKeeper;

    @Autowired
    public ButtonClickActor(LichessConsumer lichessConsumer) {
        this.broadcastConsumersKeeper = lichessConsumer;
    }

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate().getCallbackQuery() == null) {
            return;
        }
        String round = context.getUpdate().getCallbackQuery().getData().trim();
        LOG.info("Going to subscribe for a game id: {}", round);
        broadcastConsumersKeeper.subscribeForRound(round);
        context.setResponse("Вы подписаны на игру!");
    }
}
