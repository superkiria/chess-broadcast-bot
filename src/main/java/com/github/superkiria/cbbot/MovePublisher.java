package com.github.superkiria.cbbot;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.bhlangonijr.chesslib.pgn.GameLoader;
import com.github.superkiria.chess.svg.SvgBoardBuilder;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.github.superkiria.cbbot.CommentaryHelper.moveFromMovesList;
import static com.github.superkiria.cbbot.GameHelper.makeGameFromPgn;
import static com.github.superkiria.chess.svg.SvgUtils.saveDocumentToPngByteBuffer;

public class MovePublisher {

    private final ChessBroadcastBot bot;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final HashMap<String, String> opennings = new HashMap<>();
    private final static Logger LOG = LoggerFactory.getLogger(MovePublisher.class);

    public MovePublisher(ChessBroadcastBot bot) {
        this.bot = bot;
    }

    public void addPartOfPgn(String part) {
        queue.add(part);
    }

    public void startPublishing() {
        Thread thread = new Thread() {
            @SneakyThrows
            public void run() {
                while (true) {
                    List<String> buffer = new ArrayList<>();
                    String part = queue.take().trim();
                    while ( !(part.endsWith("1-0") ||
                            part.endsWith("0-1") ||
                            part.endsWith("1/2-1/2") ||
                            part.endsWith("*"))) {
                                part = queue.take().trim();
                                if (part.startsWith("[TimeControl")) {
                                    continue;
                        }
                        buffer.add(part);
                    }
                    try {
                        Game game = makeGameFromPgn(buffer);

                        String gameName = game.getWhitePlayer().getName() + " - " + game.getBlackPlayer().getName();
                        String caption = gameName + "\n";

                        int current = game.getHalfMoves().size();

                        if (current > 1) {
                            caption = caption + moveFromMovesList(game, game.getHalfMoves().size() - 1) + "\n";
                        }

                        caption = caption + moveFromMovesList(game, current);

                        if (game.getOpening() != null && (opennings.get(gameName) == null || !opennings.get(gameName).equals(game.getOpening()))) {
                            caption = caption + "\n" + game.getOpening();
                            opennings.put(gameName, game.getOpening());
                        }

                        if (!game.getResult().equals(GameResult.ONGOING)) {
                            String message = "\n" + game.getResult().value() + " " + game.getResult().getDescription();
                            caption = caption + message;
                        }

                        SvgBoardBuilder builder = new SvgBoardBuilder();
                        builder.setPgn(String.join("\n", buffer));
                        builder.init();
                        ByteArrayOutputStream baos = saveDocumentToPngByteBuffer(builder.getDocument());
                        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

                        bot.sendPhotoToChannel(inputStream, "move.png", caption);

                        LOG.info("Move sent: {}, half-move {}", gameName, current);
                    } catch (Exception e) {
                        LOG.error(String.join("\n", buffer), e);
                    } finally {
                        Thread.sleep(1000);
                    }
                }
            }
        };
        thread.start();
    }

}
