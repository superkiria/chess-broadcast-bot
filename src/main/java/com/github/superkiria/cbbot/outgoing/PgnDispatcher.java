package com.github.superkiria.cbbot.outgoing;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.superkiria.cbbot.incoming.lichess.LichessConsumer;
import com.github.superkiria.cbbot.outgoing.keepers.GameCountKeeper;
import com.github.superkiria.cbbot.outgoing.keepers.SentMessageKeeper;
import com.github.superkiria.cbbot.outgoing.model.ExtractedGame;
import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.outgoing.model.GameKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.github.superkiria.cbbot.processing.GameHelper.*;

@Component
public class PgnDispatcher {

    private final BlockingQueue<String> incoming = new LinkedBlockingQueue<>() ;
    private final MessageQueue messageQueue;
    private final static Logger LOG = LoggerFactory.getLogger(PgnDispatcher.class);
    private final SentMessageKeeper keeper;
    private final GameCountKeeper countKeeper;
    private String currentSubscriptionRoundId = null;

    @Value("${telegram.channel.chatId}")
    private String chatId;

    @Autowired
    public PgnDispatcher(MessageQueue messageQueue, SentMessageKeeper keeper, GameCountKeeper countKeeper) {
        this.messageQueue = messageQueue;
        this.keeper = keeper;
        this.countKeeper = countKeeper;
    }

    public void putPgnPart(String s) {
        incoming.add(s);
    }

    public void start() {
        LOG.info("Starting dispatching");
        new Thread(() -> {
            while (true) {
                LOG.debug("Trying to extract a game");
                try {
                    ExtractedGame extractedGame = extractNextGame();
                    GameKey key = GameKey.builder()
                            .round(extractedGame.getRound())
                            .white(extractedGame.getWhite())
                            .black(extractedGame.getBlack())
                            .build();
                    ChatContext existing = keeper.getGame(key);
                    Integer messageId;
                    int color;
                    if (existing != null) {
                        messageId = existing.getMessageId();
                        color = existing.getColor();
                    } else {
                        messageId = null;
                        color = countKeeper.getCount();
                        countKeeper.add(key);
                        LOG.info("New color {} for game {}", color, key);
                    }
                    ChatContext context = ChatContext.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .round(extractedGame.getRound())
                            .white(extractedGame.getWhite())
                            .black(extractedGame.getBlack())
                            .inputStream(makePictureFromGame(extractedGame.getGame(), color))
                            .response(makeCaptionFromGame(extractedGame.getGame()))
                            .color(color)
                            .build();
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
                    .round(currentSubscriptionRoundId)
                    .white(game.getWhitePlayer().getName())
                    .black(game.getBlackPlayer().getName())
                    .build();
            LOG.debug("Game extracted");
        } catch (RuntimeException e) {
            LOG.error(e.toString());
        }
        return extractedGame;
    }

    public void setCurrentSubscriptionRoundId(String currentSubscriptionRoundId) {
        this.currentSubscriptionRoundId = currentSubscriptionRoundId;
    }

}
