package com.github.superkiria.cbbot.incoming.lichess;

import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.incoming.lichess.model.LichessRound;
import com.github.superkiria.cbbot.outgoing.MessageQueue;
import com.github.superkiria.cbbot.outgoing.keepers.SentMessageKeeper;
import com.github.superkiria.props.TelegramProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;

@Component
public class SubscriptionManager {

    private final static Logger LOG = LoggerFactory.getLogger(SubscriptionManager.class);

    private final LichessConsumer lichess;
    private final TelegramProps telegramProps;
    private final MessageQueue messageQueue;
    private final SentMessageKeeper keeper;

    private String eventId;

    @Autowired
    public SubscriptionManager(LichessConsumer lichess, TelegramProps telegramProps, MessageQueue messageQueue, SentMessageKeeper keeper) {
        this.lichess = lichess;
        this.telegramProps = telegramProps;
        this.messageQueue = messageQueue;
        this.keeper = keeper;
    }

    public String getCurrentSubscription() {
        return eventId;
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String best = bestRoundToSubscribe();
                if (best == null) {
                    if (lichess.getCurrentSubscriptionRoundId() != null) {
                        lichess.cancelSubscription();
                    }
                    continue;
                }
                if (!best.equals(lichess.getCurrentSubscriptionRoundId())) {
                    lichess.cancelSubscription();
                    keeper.clear();
                    lichess.subscribeForRound(best);
                    messageQueue.addHighPriority(ChatContext.builder()
                            .chatId(telegramProps.getAdminChatId())
                            .response("Subscribed for: " + best)
                            .build());
                }
            }
        }).start();
    }

    public void subscribeForEvent(String eventId) {
        if (eventId == null) {
            LOG.error("eventId is not valid");
            return;
        }
        this.eventId = eventId;
    }

    public String bestRoundToSubscribe() {
        LichessRound round = nextBestRoundToSubscribe();
        if (round == null) {
            return null;
        }
        if (round.getStartsAt().before(new Date(System.currentTimeMillis() + 3600_000))) {
            return round.getId();
        }
        return null;
    }

    public LichessRound nextBestRoundToSubscribe() {
        if (eventId == null) {
            return null;
        }
        return lichess.getLichessEventById(eventId).getRounds()
                .stream()
                .filter(r -> r.getFinished() == null || !r.getFinished())
                .min(Comparator.comparing(LichessRound::getStartsAt))
                .orElse(null);
    }

    public void cancelSubscription() {
        eventId = null;
        lichess.cancelSubscription();
        keeper.clear();
        messageQueue.clear();
    }

}
