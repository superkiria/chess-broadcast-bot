package com.github.superkiria.cbbot.processing;

import com.github.bhlangonijr.chesslib.game.*;
import com.github.superkiria.cbbot.admin.SubscriptionManager;
import com.github.superkiria.cbbot.processing.message.MoveMarkedCaptionConstructor;
import com.github.superkiria.cbbot.sending.MessageQueue;
import com.github.superkiria.cbbot.sending.SentDataKeeper;
import com.github.superkiria.cbbot.processing.model.GameMoveInfo;
import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.processing.model.GameKey;
import com.github.superkiria.cbbot.model.MarkedCaption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.github.superkiria.cbbot.main.Stickers.getRandomStickerId;
import static com.github.superkiria.cbbot.processing.message.ChatContextHelper.makeChatContext;
import static com.github.superkiria.cbbot.processing.message.ComposeMessageHelper.newRoundMessage;
import static com.github.superkiria.cbbot.processing.message.GameKeyHelper.gameKeyFromExtractedGame;
import static com.github.superkiria.cbbot.processing.message.PictureHelper.makePictureFromGame;

@Component
public class PublicationProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(PublicationProcessor.class);

    @Value("${telegram.channel.chatId}")
    private String chatId;

    private final MessageQueue messageQueue;
    private final SentDataKeeper keeper;
    private final SubscriptionManager subscriptionManager;
    private final MoveMarkedCaptionConstructor moveMarkedCaptionConstructor;
    private final MoveProcessor moveProcessor;
    private final Set<String> publishedRounds = new HashSet<>();

    @Autowired
    public PublicationProcessor(MessageQueue messageQueue, SentDataKeeper keeper, SubscriptionManager subscriptionManager, MoveMarkedCaptionConstructor moveMarkedCaptionConstructor, MoveProcessor moveProcessor) {
        this.messageQueue = messageQueue;
        this.keeper = keeper;
        this.subscriptionManager = subscriptionManager;
        this.moveMarkedCaptionConstructor = moveMarkedCaptionConstructor;
        this.moveProcessor = moveProcessor;
    }

    @EventListener
    public void start(ApplicationReadyEvent event) {
        LOG.info("Starting dispatching");
        new Thread(() -> {
            while (true) {
                LOG.debug("Trying to extract a game");
                GameMoveInfo gameMoveInfo = null;
                ChatContext context = null;
                try {

                    List<GameMoveInfo> extractedMoves = moveProcessor.getMovesForNextGame();

                    if (extractedMoves.isEmpty()) {
                        continue;
                    }

                    sendRoundAnnouncementIfNotSent();

                    for (GameMoveInfo move : extractedMoves) {
                        GameKey key = gameKeyFromExtractedGame(move);
                        checkMoveGameInfoAndFallbackIfNecessary(move, key);
                        MarkedCaption caption = moveMarkedCaptionConstructor.makeMarkedCaptionFromGame(move.getGame(), key, move.getHalfMove());
                        MarkedCaption shortCaption = moveMarkedCaptionConstructor.makeMarkedCaptionFromGame(move.getGame(), key, move.getHalfMove(), true);
                        context = makeChatContext(chatId, move, caption, shortCaption, key);
                        Integer color = getColorForBoard(key);
                        context.setColor(color);
                        context.setInputStream(makePictureFromGame(move.getGame(), color, move.getHalfMove()));
                        messageQueue.add(context);
                        LOG.info("Move prepared {} {}", key, move.getHalfMove());
                    }
                } catch (Exception e) {
                    LOG.error("Error on making a move: ", e);
                    LOG.error("Extracted game was: " + gameMoveInfo);
                    LOG.error("ChatContext was: " + context);
                }
            }
        }).start();
    }

    private void checkMoveGameInfoAndFallbackIfNecessary(GameMoveInfo gameMoveInfo, GameKey key) {
        if (gameMoveInfo.getGame() != null) {
            keeper.putLastValidGame(key, gameMoveInfo);
        } else if (gameMoveInfo.getGameResult() != null && keeper.getLastValidGame(key) != null) {
            Game fallbackGame = keeper.getLastValidGame(key).getGame();
            fallbackGame.setResult(gameMoveInfo.getGameResult());
            gameMoveInfo.setGame(fallbackGame);
            gameMoveInfo.setHalfMove(fallbackGame.getHalfMoves().size() - 1);
        }
    }

    private Integer getColorForBoard(GameKey key) {
        if (keeper.getColor(key) == null) {
            keeper.putColor(key, keeper.getColorsCount());
            LOG.debug("New color {} for game {}", keeper.getColor(key), key);
        }
        return keeper.getColor(key);
    }

    private void sendRoundAnnouncementIfNotSent() {
        if (subscriptionManager.currentRound() != null
                && !publishedRounds.contains(subscriptionManager.currentRound().getId())) {
            moveProcessor.reset();
            messageQueue.add(ChatContext.builder().stickerId(getRandomStickerId()).chatId(chatId).build());
            messageQueue.add(newRoundMessage(subscriptionManager.currentEvent(), subscriptionManager.currentRound(), chatId));
            publishedRounds.add(subscriptionManager.currentRound().getId());
        }
    }

}
