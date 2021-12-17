package com.github.superkiria.cbbot.outgoing;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.superkiria.cbbot.outgoing.model.ExtractedGame;
import com.github.superkiria.cbbot.chatchain.ChatContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.github.superkiria.cbbot.processing.GameHelper.makeGameFromPgn;

@Component
public class PgnDispatcher {

    private final BlockingQueue<String> incoming = new LinkedBlockingQueue<>() ;
    private final MessageQueue messageQueue;
    private final static Logger LOG = LoggerFactory.getLogger(PgnDispatcher.class);

    @Value("telegram.channel.chatId")
    private static String chatId;

    @Autowired
    public PgnDispatcher(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void putPgnPart(String s) {
        incoming.add(s);
    }

    public void start() {
        LOG.info("Starting dispatching");
        new Thread(() -> {
            while (true) {
                try {
                    ExtractedGame extractedGame = extractNextGame();
                    LOG.info("ExtractedGame extractedGame = extractNextGame();");
                    messageQueue.add(ChatContext.builder().response(SendMessage.builder().chatId(chatId).text(extractedGame.getGameId()).build()).build());
                } catch (InterruptedException e) {
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
            LOG.info(part);
        } while (!(part.endsWith("1-0") ||
                    part.endsWith("0-1") ||
                    part.endsWith("1/2-1/2") ||
                    part.endsWith("*")));
        ExtractedGame extractedGame = null;
        try {
            Game game = makeGameFromPgn(buffer);
            extractedGame = ExtractedGame.builder().game(game)
                    .gameId(game.getRound().getEvent().getName()
                            + " | " + game.getRound().getNumber()
                            + " | " + game.getWhitePlayer().getName()
                            + " | " + game.getBlackPlayer().getName())
                    .build();
            LOG.info("Game extracted");
        } catch (RuntimeException e) {
            LOG.error(e.toString());
        }
        return extractedGame;
    }

}
