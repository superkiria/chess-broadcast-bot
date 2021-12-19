package com.github.superkiria.cbbot.processing;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.bhlangonijr.chesslib.pgn.GameLoader;
import com.github.superkiria.chess.svg.SvgBoardBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import static com.github.superkiria.cbbot.processing.CommentaryHelper.moveFromMovesList;
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
            String message = "\n" + game.getResult().value() + " " + game.getResult().getDescription();
            caption = caption + message;
        }
        return caption;
    }

}
