package com.github.superkiria.cbbot.broadcast;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.superkiria.cbbot.queue.MessageQueue;
import com.github.superkiria.cbbot.queue.MessageQueueObject;
import com.github.superkiria.chatchain.ChatContext;
import com.github.superkiria.chess.svg.SvgBoardBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.github.superkiria.cbbot.CommentaryHelper.moveFromMovesList;
import static com.github.superkiria.cbbot.GameHelper.makeGameFromPgn;
import static com.github.superkiria.chess.svg.SvgUtils.saveDocumentToPngByteBuffer;

public class ChatBroadcaster {

    private List<String> pgnPartQueue = new ArrayList<>();
    private final HashMap<String, String> openings = new HashMap<>();
    private final MessageQueue messageQueue;
    private final String chatId;
    private final static Logger LOG = LoggerFactory.getLogger(ChatBroadcaster.class);
    private final String round;
    private boolean active = false;

    public ChatBroadcaster(MessageQueue messageQueue, String chatId, String round) {
        this.messageQueue = messageQueue;
        this.chatId = chatId;
        this.round = round;
        LOG.info("Broadcaster for chatId {}, round {} created", chatId, round);
    }

    public void addPartOfPgn(String s) {
        if (!active) {
            LOG.info("Broadcaster for chatId {}, round {} recieves str: {}", chatId, round, s);
            active = true;
        }
//        synchronized (this) {
//            String part = s.trim();
//            pgnPartQueue.add(part);
//            if (!(part.endsWith("1-0") ||
//                    part.endsWith("0-1") ||
//                    part.endsWith("1/2-1/2") ||
//                    part.endsWith("*"))) {
//                return;
//            }
//            if (part.trim().equals("*")) {
//                pgnPartQueue = new ArrayList<>();
//                return;
//            }
//            try {
//                Game game = makeGameFromPgn(pgnPartQueue);
//
//                String gameName = game.getWhitePlayer().getName() + " - " + game.getBlackPlayer().getName();
//                String caption = gameName + "\n";
//
//                int current = game.getHalfMoves().size();
//
//                if (current > 1) {
//                    caption = caption + moveFromMovesList(game, game.getHalfMoves().size() - 1) + "\n";
//                }
//
//                caption = caption + moveFromMovesList(game, current);
//
//                if (game.getOpening() != null && (openings.get(gameName) == null || !openings.get(gameName).equals(game.getOpening()))) {
//                    caption = caption + "\n" + game.getOpening();
//                    openings.put(gameName, game.getOpening());
//                }
//
//                if (!game.getResult().equals(GameResult.ONGOING)) {
//                    String message = "\n" + game.getResult().value() + " " + game.getResult().getDescription();
//                    caption = caption + message;
//                }
//
//                SvgBoardBuilder builder = new SvgBoardBuilder();
//                builder.setPgn(String.join("\n", pgnPartQueue));
//                builder.init();
//                ByteArrayOutputStream baos = saveDocumentToPngByteBuffer(builder.getDocument());
//                InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
//
//                SendPhoto message = SendPhoto
//                        .builder()
//                        .caption(caption)
//                        .photo(new InputFile(inputStream, "move.png"))
//                        .chatId(chatId)
//                        .build();
//
//                messageQueue.add(MessageQueueObject.builder().chatId(chatId).context(ChatContext.builder().sendPhoto(message).build()).build());
//
//                pgnPartQueue = new ArrayList<>();
//
//                LOG.info("Move sent: {}, half-move {}", gameName, current);
//            } catch (Exception e) {
//                LOG.error(String.join("\n", pgnPartQueue), e);
//            }
//        }
    }

}
