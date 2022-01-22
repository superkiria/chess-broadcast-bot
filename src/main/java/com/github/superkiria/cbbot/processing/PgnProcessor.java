package com.github.superkiria.cbbot.processing;

import com.github.bhlangonijr.chesslib.game.*;
import com.github.bhlangonijr.chesslib.pgn.PgnProperty;
import com.github.superkiria.cbbot.admin.SubscriptionManager;
import com.github.superkiria.cbbot.sending.MessageQueue;
import com.github.superkiria.cbbot.sending.keepers.SentDataKeeper;
import com.github.superkiria.cbbot.sending.model.ExtractedGame;
import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.sending.model.GameKey;
import com.github.superkiria.cbbot.sending.model.MarkedCaption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.github.bhlangonijr.chesslib.pgn.PgnProperty.parsePgnProperty;
import static com.github.superkiria.cbbot.main.Stickers.getRandomStickerId;
import static com.github.superkiria.cbbot.processing.ComposeMessageHelper.newRoundMessage;
import static com.github.superkiria.cbbot.processing.GameHelper.makeGameFromPgn;
import static com.github.superkiria.cbbot.processing.GameHelper.makePictureFromGame;

@Component
public class PgnProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(PgnProcessor.class);

    @Value("${telegram.channel.chatId}")
    private String chatId;

    private final MessageQueue messageQueue;
    private final SentDataKeeper keeper;
    private final PgnQueue incoming;
    private final SubscriptionManager subscriptionManager;
    private final GameHelper gameHelper;

    private final Set<String> publishedRounds = new HashSet<>();

    @Autowired
    public PgnProcessor(MessageQueue messageQueue, SentDataKeeper keeper, PgnQueue incoming, SubscriptionManager subscriptionManager, GameHelper gameHelper) {
        this.messageQueue = messageQueue;
        this.keeper = keeper;
        this.incoming = incoming;
        this.subscriptionManager = subscriptionManager;
        this.gameHelper = gameHelper;
    }

    @EventListener
    public void start(ApplicationReadyEvent event) {
        LOG.info("Starting dispatching");
        new Thread(() -> {
            while (true) {
                LOG.debug("Trying to extract a game");
                ExtractedGame extractedGame = null;
                try {
                    extractedGame = extractNextGame();
                    sendRoundAnnouncementIfNotSent();
                    GameKey key = gameKeyFromExtractedGame(extractedGame);
                    checkExtractedGameAndFallbackIfNecessary(extractedGame, key);
                    MarkedCaption caption = gameHelper.makeMarkedCaptionFromGame(extractedGame.getGame(), key);
                    MarkedCaption shortCaption = gameHelper.makeMarkedCaptionFromGame(extractedGame.getGame(), key, true);
                    ChatContext context = makeChatContext(extractedGame, caption, shortCaption, key);
                    Integer color = getColorForBoard(key);
                    context.setColor(color);
                    context.setInputStream(makePictureFromGame(extractedGame.getGame(), color));
                    messageQueue.add(context);
                } catch (Exception e) {
                    LOG.error("Error on making a move: ", e);
                    LOG.error(String.valueOf(extractedGame));
                }
            }
        }).start();
    }

    private ChatContext makeChatContext(ExtractedGame extractedGame, MarkedCaption caption, MarkedCaption shortCaption, GameKey key) {
        return ChatContext.builder()
                .chatId(chatId)
                .round(extractedGame.getRound())
                .white(extractedGame.getWhite())
                .black(extractedGame.getBlack())
                .response(caption.getCaption())
                .entities(caption.getEntities())
                .shortMarkedCaption(shortCaption)
                .key(key)
                .opening(extractedGame.getGame().getOpening())
                .build();
    }

    private void checkExtractedGameAndFallbackIfNecessary(ExtractedGame extractedGame, GameKey key) {
        if (extractedGame.getGame() != null) {
            keeper.putLastValidGame(key, extractedGame);
        } else if (extractedGame.getGameResult() != null && keeper.getLastValidGame(key) != null) {
            Game fallbackGame = keeper.getLastValidGame(key).getGame();
            fallbackGame.setResult(extractedGame.getGameResult());
            extractedGame.setGame(fallbackGame);
        }
    }

    private GameKey gameKeyFromExtractedGame(ExtractedGame extractedGame) {
        return GameKey.builder()
                .round(extractedGame.getRound())
                .white(extractedGame.getWhite())
                .black(extractedGame.getBlack())
                .build();
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
            messageQueue.add(ChatContext.builder().stickerId(getRandomStickerId()).chatId(chatId).build());
            messageQueue.add(newRoundMessage(subscriptionManager.currentEvent(), subscriptionManager.currentRound(), chatId));
            publishedRounds.add(subscriptionManager.currentRound().getId());
        }
    }

    private ExtractedGame extractNextGame() throws InterruptedException {
        List<String> pgn = waitAndPreparePgn();
        ExtractedGame extractedGame;
        try {
            Game game = makeGameFromPgn(pgn);
            extractedGame = ExtractedGame.builder()
                    .game(game)
                    .round(subscriptionManager.getCurrentSubscription())
                    .white(game.getWhitePlayer().getName())
                    .black(game.getBlackPlayer().getName())
                    .build();
            LOG.debug("Game extracted");
        } catch (Exception e) {
            LOG.error("Game extraction failed", e);
            LOG.debug(String.join("\n", pgn));
            extractedGame = fallbackGameExtraction(pgn);
        }
        return extractedGame;
    }

    private ExtractedGame fallbackGameExtraction(List<String> pgn) {
        ExtractedGame.ExtractedGameBuilder builder = ExtractedGame.builder();
        builder.round(subscriptionManager.getCurrentSubscription());
        for (String str : pgn) {
            if (PgnProperty.isProperty(str)) {
                PgnProperty pgnProperty = parsePgnProperty(str);
                if (pgnProperty.name.equalsIgnoreCase("white")) {
                    builder.white(pgnProperty.value);
                }
                if (pgnProperty.name.equalsIgnoreCase("black")) {
                    builder.black(pgnProperty.value);
                }
                continue;
            }
            if (str.endsWith("*")) {
                builder.gameResult(GameResult.ONGOING);
                continue;
            }
            if (str.endsWith("1/2-1/2")) {
                builder.gameResult(GameResult.DRAW);
                continue;
            }
            if (str.endsWith("1-0")) {
                builder.gameResult(GameResult.WHITE_WON);
                continue;
            }
            if (str.endsWith("0-1")) {
                builder.gameResult(GameResult.BLACK_WON);
            }
        }
        return builder.build();
    }

    private List<String> waitAndPreparePgn() throws InterruptedException {
        LinkedList<String> buffer = new LinkedList<>();
        String part;
        boolean hasEventInfo = false;
        do {
            part = incoming.take().trim();
            if (PgnProperty.isProperty(part) && parsePgnProperty(part).name.equalsIgnoreCase("event")) {
                hasEventInfo = true;
            }
            if (PgnProperty.isProperty(part) && parsePgnProperty(part).name.equalsIgnoreCase("TimeControl")) {
                LOG.trace("Skipped: {}", part);
                continue;
            }
            buffer.add(part);
            LOG.trace(part);
        } while (!(part.endsWith("*") ||
                part.endsWith("0-1") ||
                part.endsWith("1/2-1/2") ||
                part.endsWith("1-0")));
        if (!hasEventInfo) {
            buffer.addFirst("[Event \"Fake event 2022\"]");
        }
        return buffer;
    }

}
