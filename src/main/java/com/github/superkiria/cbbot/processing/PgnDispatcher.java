package com.github.superkiria.cbbot.processing;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.superkiria.cbbot.admin.SubscriptionManager;
import com.github.superkiria.cbbot.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.lichess.model.LichessRound;
import com.github.superkiria.cbbot.sending.MessageQueue;
import com.github.superkiria.cbbot.sending.keepers.SentMessageKeeper;
import com.github.superkiria.cbbot.sending.model.ExtractedGame;
import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.sending.model.GameKey;
import com.github.superkiria.cbbot.sending.model.MarkedCaption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.superkiria.cbbot.processing.GameHelper.*;

@Component
public class PgnDispatcher {

    private final static Logger LOG = LoggerFactory.getLogger(PgnDispatcher.class);

    @Value("${telegram.channel.chatId}")
    private String chatId;

    private final MessageQueue messageQueue;
    private final SentMessageKeeper keeper;
    private final PgnQueue incoming;
    private final SubscriptionManager subscriptionManager;

    private final Set<String> publishedRounds = new HashSet<>();

    @Autowired
    public PgnDispatcher(MessageQueue messageQueue, SentMessageKeeper keeper, PgnQueue incoming, SubscriptionManager subscriptionManager) {
        this.messageQueue = messageQueue;
        this.keeper = keeper;
        this.incoming = incoming;
        this.subscriptionManager = subscriptionManager;
    }

    public void start() {
        LOG.info("Starting dispatching");
        new Thread(() -> {
            while (true) {
                LOG.debug("Trying to extract a game");
                try {
                    ExtractedGame extractedGame = extractNextGame();

                    if (subscriptionManager.currentRound() != null
                            && !publishedRounds.contains(subscriptionManager.currentRound().getId())) {
                        LichessEvent lichessEvent = subscriptionManager.currentEvent();
                        LichessRound lichessRound = subscriptionManager.currentRound();
                            if (lichessEvent != null && lichessEvent.getTour() != null && lichessRound != null) {
                                String announcement = lichessEvent.getTour().getName() + " - "
                                        + lichessRound.getName() + "\n"
                                        + lichessEvent.getTour().getDescription() + "\n"
                                        + lichessEvent.getTour().getUrl() + "\n"
                                        + lichessRound.getUrl();
                                messageQueue.add(ChatContext.builder()
                                        .chatId(chatId).response(announcement).build());
                                publishedRounds.add(subscriptionManager.currentRound().getId());
                            }
                    }

                    GameKey key = GameKey.builder()
                            .round(extractedGame.getRound())
                            .white(extractedGame.getWhite())
                            .black(extractedGame.getBlack())
                            .build();
                    MarkedCaption markedCaption = makeMarkedCaptionFromGame(extractedGame.getGame());
                    ChatContext context = ChatContext.builder()
                            .chatId(chatId)
                            .round(extractedGame.getRound())
                            .white(extractedGame.getWhite())
                            .black(extractedGame.getBlack())
                            .response(markedCaption.getCaption())
                            .entities(markedCaption.getEntities())
                            .key(key)
                            .build();
                    ChatContext existing = keeper.getGame(key);
                    int color;
                    if (existing != null) {
                        color = existing.getColor();
                    } else {
                        color = keeper.getCount();
                        keeper.putGame(key, context);
                        LOG.info("New color {} for game {}", color, key);
                    }
                    context.setColor(color);
                    context.setInputStream(makePictureFromGame(extractedGame.getGame(), color));
                    messageQueue.add(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private ExtractedGame extractNextGame() throws InterruptedException {
        List<String> buffer = new ArrayList<>();
        String part;
        do {
            part = incoming.take().trim();
            buffer.add(part);
            LOG.trace(part);
        } while (!(part.endsWith("1-0") ||
                    part.endsWith("0-1") ||
                    part.endsWith("1/2-1/2") ||
                    part.endsWith("*")));
        ExtractedGame extractedGame = null;
        try {
            Game game = makeGameFromPgn(buffer);
            extractedGame = ExtractedGame.builder()
                    .game(game)
                    .round(subscriptionManager.getCurrentSubscription())
                    .white(game.getWhitePlayer().getName())
                    .black(game.getBlackPlayer().getName())
                    .build();
            LOG.debug("Game extracted");
        } catch (RuntimeException e) {
            LOG.error(e.toString());
        }
        return extractedGame;
    }

}
