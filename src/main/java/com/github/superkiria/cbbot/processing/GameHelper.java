package com.github.superkiria.cbbot.processing;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.bhlangonijr.chesslib.pgn.GameLoader;
import com.github.superkiria.cbbot.outgoing.model.MarkedCaption;
import com.github.superkiria.chess.svg.SvgBoardBuilder;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.superkiria.cbbot.processing.CommentaryHelper.moveFromMovesList;
import static com.github.superkiria.cbbot.processing.CommentaryHelper.timeFromMovesList;
import static com.github.superkiria.chess.svg.SvgUtils.saveDocumentToPngByteBuffer;

public class GameHelper {

    public static Game makeGameFromPgn(List<String> buffer) {
        Game game = GameLoader.loadNextGame(buffer.listIterator());
        game.setBoard(new Board());
        game.gotoLast();
        return game;
    }

    public static InputStream makePictureFromGame(Game game, Integer color) throws Exception {
        SvgBoardBuilder builder = new SvgBoardBuilder();
        builder.setPgn(game.toPgn(false, false));
        builder.init(color);
        ByteArrayOutputStream baos = saveDocumentToPngByteBuffer(builder.getDocument());
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static String makeCaptionFromGame(Game game) {
        String gameName = game.getWhitePlayer().getName() + " - " + game.getBlackPlayer().getName();
        String caption = gameName + "\n";

        int current = game.getHalfMoves().size();

        if (current > 1) {
            caption = caption + moveFromMovesList(game, game.getHalfMoves().size() - 1) + "\n";
        }

        caption = caption + moveFromMovesList(game, current);

        caption = caption + "\n" + game.getOpening();

        if (!game.getResult().equals(GameResult.ONGOING)) {
            String message = "";
            switch (game.getResult()) {
                case WHITE_WON:
                    message = "🏳️🏳️🏳️ White's victory";
                    break;
                case BLACK_WON:
                    message = "🏴🏴🏴 Black's victory";
                    break;
                case DRAW:
                    message = "🏁👔🏁 Draw";
                    break;
            }
            caption = caption + "\n" + message;
        }

        if (game.getRound() != null && game.getRound().getEvent() != null) {
            caption = caption + "\n" + game.getRound().getEvent().getSite();
        }

        return caption;
    }

    public static MarkedCaption makeMarkedCaptionFromGame(Game game) {
        StringBuilder caption = new StringBuilder();
        List<MessageEntity> list = new ArrayList<>();

        int offset = 0;

        if (!game.getResult().equals(GameResult.ONGOING)) {
            String result = "";
            switch (game.getResult()) {
                case WHITE_WON:
                    result = "🏳️️ White's victory\n";
                    break;
                case BLACK_WON:
                    result = "🏴 Black's victory\n";
                    break;
                case DRAW:
                    result = "👔 Draw\n";
                    break;
            }
            list.add(MessageEntity.builder()
                    .type("bold")
                    .text(result)
                    .offset(offset)
                    .length(result.length())
                    .build());
            offset += result.length();
            caption.append(result);
        }

        int current = game.getHalfMoves().size();

        if (current > 1) {
            String previousMove = moveFromMovesList(game, game.getHalfMoves().size() - 1) + "\n";
            list.add(MessageEntity.builder()
                    .type("code")
                    .text(previousMove)
                    .offset(offset)
                    .length(previousMove.length())
                    .build());
            offset += previousMove.length();
            caption.append(previousMove);
        }

        String currentMove = moveFromMovesList(game, current) + "\n";
        list.add(MessageEntity.builder()
                .type("code")
                .text(currentMove)
                .offset(offset)
                .length(currentMove.length())
                .build());
        offset += currentMove.length();
        caption.append(currentMove);

        if (current > 1) {
            String time;
            if (current % 2 == 0) {
                time = timeFromMovesList(game, current - 1) + " ⏱ " + timeFromMovesList(game, current) + "\n";
            } else {
                time = timeFromMovesList(game, current) + " ⏱ " + timeFromMovesList(game, current - 1) + "\n";
            }
            list.add(MessageEntity.builder()
                    .type("code")
                    .text(time)
                    .offset(offset)
                    .length(time.length())
                    .build());
            offset += time.length();
            caption.append(time);
        }

        String gameName = game.getWhitePlayer().getName() + " - " + game.getBlackPlayer().getName() + "\n";
//        list.add(MessageEntity.builder()
//                        .type("underline")
//                        .text(gameName)
//                        .offset(offset)
//                        .length(gameName.length())
//                        .build());
        offset += gameName.length();
        caption.append(gameName);

        String opening = game.getOpening() + "\n";
        list.add(MessageEntity.builder()
                .type("italic")
                .text(opening)
                .offset(offset)
                .length(opening.length())
                .build());
        offset += opening.length();
        caption.append(opening);

        if (game.getRound() != null && game.getRound().getEvent() != null) {
            String site = game.getRound().getEvent().getSite();
            String linkText = "lichess";
            list.add(MessageEntity.builder()
                    .type("text_link")
                    .text(linkText)
                    .url(site)
                    .offset(offset)
                    .length(linkText.length())
                    .build());
            caption.append(linkText);
        }

        return MarkedCaption.builder().caption(caption.toString()).entities(list).build();

    }

}
