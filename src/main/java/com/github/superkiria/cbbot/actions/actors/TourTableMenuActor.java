package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.lichess.LichessConsumer;
import com.github.superkiria.cbbot.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.sending.MessageQueue;
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
    private final static int PORTION = 5;

    private final LichessConsumer broadcastConsumer;
    private final MessageQueue messageQueue;

    @Autowired
    public TourTableMenuActor(LichessConsumer broadcastConsumer, MessageQueue messageQueue) {
        this.broadcastConsumer = broadcastConsumer;
        this.messageQueue = messageQueue;
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

        for(int i = 0; i <= (ongoingTours.size() - 1) / PORTION; i++) {
            sendTours(ongoingTours.subList(i * PORTION, Math.min(i * PORTION + PORTION, ongoingTours.size())), context);
        }

    }

    private void sendTours(List<LichessEvent> tours, ChatContext context) {
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder markup = InlineKeyboardMarkup.builder();
        for(LichessEvent event : tours) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .callbackData("tour:" + event.getTour().getId())
                    .text(event.getTour().getName())
                    .build();
            markup.keyboardRow(Collections.singletonList(button));
        }
        ChatContext newContext = ChatContext.builder().chatId(context.getChatId()).inlineKeyboardMarkup(markup.build())
                .markedCaption(MarkedCaption.builder()
                .caption("Found " + tours.size()
                        + " tours:\n\n"
                        + tours.stream().map(t -> t.getTour().getName() /*+ "\n" + t.getTour().getDescription() + "\n" + t.getTour().getUrl()*/)
                        .collect(Collectors.joining("\n"))).build())
                .build();
        messageQueue.add(newContext);
    }

}
