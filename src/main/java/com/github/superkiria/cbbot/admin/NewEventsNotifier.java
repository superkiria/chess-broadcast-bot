package com.github.superkiria.cbbot.admin;

import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.lichess.LichessConsumer;
import com.github.superkiria.cbbot.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.sending.MessageQueue;
import com.github.superkiria.cbbot.model.MarkedCaption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.superkiria.cbbot.processing.message.ComposeMessageHelper.eventInfo;

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

    @EventListener
    public void start(ApplicationReadyEvent e) {
        List<LichessEvent> currentBroadcasts = lichessConsumer.getActualLichessBroadcasts();
        for (LichessEvent event : currentBroadcasts) {
            sentNotifications.add(event.getTour().getId());
        }
        ChatContext context = ChatContext.builder()
                .markedCaption(MarkedCaption.builder().caption("🍀🍀🍀 the init of NewEventsNotifier").build())
                .chatId(adminChatId)
                .build();
        messageQueue.add(context);
    }

    @Scheduled(fixedDelay = 1214_000, initialDelay = 60_000)
    void start() {
        List<LichessEvent> currentBroadcasts = lichessConsumer.getActualLichessBroadcasts();
        for (LichessEvent event : currentBroadcasts) {
            if (!sentNotifications.contains(event.getTour().getId())) {
                ChatContext context = ChatContext.builder()
                        .markedCaption(MarkedCaption.builder().caption("🍀 " + eventInfo(event)).build())
                        .chatId(adminChatId)
                        .build();
                messageQueue.add(context);
                sentNotifications.add(event.getTour().getId());
            }
        }
    }

}
