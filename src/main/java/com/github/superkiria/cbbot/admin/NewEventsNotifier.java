package com.github.superkiria.cbbot.admin;

import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.lichess.LichessConsumer;
import com.github.superkiria.cbbot.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.sending.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.superkiria.cbbot.processing.ComposeMessageHelper.eventInfo;
import static com.github.superkiria.cbbot.processing.ComposeMessageHelper.eventSubscribeButton;

@Component
public class NewEventsNotifier {

    @Value("${telegram.admin.chatId}")
    private String adminChatId;

    private final LichessConsumer lichessConsumer;
    private final MessageQueue messageQueue;

    private final Set<String> sentNotifications = new HashSet<>();

    @Autowired
    public NewEventsNotifier(LichessConsumer lichessConsumer, MessageQueue messageQueue) {
        this.lichessConsumer = lichessConsumer;
        this.messageQueue = messageQueue;
    }

    @Scheduled(fixedDelay = 3600_000, initialDelay = 10_000)
    void start() {
        List<LichessEvent> currentBroadcasts = lichessConsumer.getActualLichessBroadcasts();
        for (LichessEvent event : currentBroadcasts) {
            if (!sentNotifications.contains(event.getTour().getId())) {
                ChatContext context = ChatContext.builder()
                        .response(eventInfo(event))
                        .inlineKeyboardMarkup(eventSubscribeButton(event.getTour().getId()))
                        .chatId(adminChatId)
                        .build();
                messageQueue.add(context);
                sentNotifications.add(event.getTour().getId());
            }
        }
    }

}
