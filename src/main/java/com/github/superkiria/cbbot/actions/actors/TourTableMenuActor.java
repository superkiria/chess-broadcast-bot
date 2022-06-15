package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.lichess.model.LichessRound;
import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.lichess.LichessConsumer;
import com.github.superkiria.cbbot.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.sending.MessageQueue;
import com.github.superkiria.cbbot.model.MarkedCaption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TourTableMenuActor implements ChatActor {

    private final static Logger LOG = LoggerFactory.getLogger(ButtonClickActor.class);
    private final static int PORTION = 5;
    private final static String DELIMITER = "\n=== ";

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
        int pages = (ongoingTours.size() - 1) / PORTION + 1;
        for(int i = 0; i < pages; i++) {
            sendTours(ongoingTours.subList(i * PORTION, Math.min(i * PORTION + PORTION, ongoingTours.size())), context, i + 1, pages);
        }

    }

    private void sendTours(List<LichessEvent> tours, ChatContext context, int page, int pages) {
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
                .caption(tours.size() + " tours - page " + page + "/" + pages
                        + DELIMITER
                        + tours.stream().map(t -> t.getTour().getName()
                                + " || "
                                + "" + t.getRounds().stream().filter(r -> r.getFinished() != null).count()
                                + "/" + t.getRounds().size()
                                + " ~" + t.getRounds().stream().filter(r -> r.getOngoing() != null).count()

                        )
                        .collect(Collectors.joining(DELIMITER))
                ).build()).build();
        messageQueue.add(newContext);
    }

}
