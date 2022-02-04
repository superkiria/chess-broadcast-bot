package com.github.superkiria.cbbot.admin;

import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.lichess.LichessConsumer;
import com.github.superkiria.cbbot.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.lichess.model.LichessRound;
import com.github.superkiria.cbbot.sending.MessageQueue;
import com.github.superkiria.cbbot.sending.keepers.SentDataKeeper;
import com.github.superkiria.cbbot.sending.model.MarkedCaption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;

@Component
public class SubscriptionManager {

    private final static Logger LOG = LoggerFactory.getLogger(SubscriptionManager.class);

    private final LichessConsumer lichess;
    private final MessageQueue messageQueue;
    private final SentDataKeeper keeper;

    @Value("${telegram.admin.chatId}")
    private String adminChatId;

    private String eventId;

    @Autowired
    public SubscriptionManager(LichessConsumer lichess, MessageQueue messageQueue, SentDataKeeper keeper) {
        this.lichess = lichess;
        this.messageQueue = messageQueue;
        this.keeper = keeper;
    }

    public String getCurrentSubscription() {
        return eventId;
    }

    @Scheduled(fixedDelay = 30_000, initialDelay = 10_000)
    public void start() {
        String best = bestRoundToSubscribe();
        if (best == null) {
            if (lichess.getCurrentSubscriptionRoundId() != null) {
                lichess.cancelSubscription();
            }
            return;
        }
        if (!best.equals(lichess.getCurrentSubscriptionRoundId())) {
            lichess.subscribeForRound(best);
            messageQueue.add(ChatContext.builder()
                    .chatId(adminChatId)
                    .markedCaption(MarkedCaption.builder().caption("Subscribed for: " + best).build())
                    .build());
        }
    }

    public void subscribeForEvent(String eventId) {
        if (eventId == null) {
            LOG.error("eventId is not valid");
            return;
        }
        if (!eventId.equals(this.eventId)) {
            cancelSubscription();
            this.eventId = eventId;
        }
    }

    public String bestRoundToSubscribe() {
        LichessRound round = nextBestRoundToSubscribe();
        if (round == null) {
            return null;
        }
        if (round.getStartsAt().before(new Date(System.currentTimeMillis() + 37_000))) {
            return round.getId();
        }
        return null;
    }

    public LichessRound nextBestRoundToSubscribe() {
        if (eventId == null) {
            return null;
        }
        LichessEvent lichessEventById = lichess.getLichessEventById(eventId);
        return lichessEventById != null ?
                    lichessEventById.getRounds()
                    .stream()
                    .filter(r -> r.getFinished() == null || !r.getFinished())
                    .min(Comparator.comparing(LichessRound::getStartsAt))
                    .orElse(null)
                : null;
    }

    public LichessEvent currentEvent() {
        if (eventId == null) {
            return null;
        }
        return lichess.getLichessEventById(eventId);
    }

    public LichessRound currentRound() {
        return nextBestRoundToSubscribe();
    }

    public void cancelSubscription() {
        eventId = null;
        lichess.cancelSubscription();
        keeper.clear();
        messageQueue.clear();
    }

}
