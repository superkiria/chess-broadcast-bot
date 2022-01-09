package com.github.superkiria.cbbot.processing;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.bhlangonijr.chesslib.pgn.GameLoader;
import com.github.superkiria.cbbot.sending.model.MarkedCaption;
import com.github.superkiria.chess.svg.SvgBoardBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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

    public static MarkedCaption makeMarkedCaptionFromGame(Game game) {
        CaptionMarkupConstructor constructor = new CaptionMarkupConstructor();

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
            constructor.addString(result, "bold");
        }

        int current = game.getHalfMoves().size();

        if (current > 1) {
            String previousMove = moveFromMovesList(game, game.getHalfMoves().size() - 1) + "\n";
            constructor.addString(previousMove, "code");
        }

        String currentMove = moveFromMovesList(game, current) + "\n";
        constructor.addString(currentMove, "code");

        if (current > 1) {
            String time;
            if (current % 2 == 0) {
                time = timeFromMovesList(game, current - 1) + " ⏱ " + timeFromMovesList(game, current) + "\n";
            } else {
                time = timeFromMovesList(game, current) + " ⏱ " + timeFromMovesList(game, current - 1) + "\n";
            }
            constructor.addString(time, null);
        }

        String gameName = game.getWhitePlayer().getName() + " - " + game.getBlackPlayer().getName() + "\n";
        constructor.addString(gameName, "bold");

        String opening = game.getOpening() + "\n";
        constructor.addString(opening, "italic");

        if (game.getRound() != null
                && game.getRound().getEvent() != null
                && game.getRound().getEvent().getSite() != null
                && game.getRound().getEvent().getSite().trim().startsWith("http")) {
            constructor.addLink("check on lichess", game.getRound().getEvent().getSite());
        }

        return MarkedCaption.builder().caption(constructor.getCaption()).entities(constructor.getEntities()).build();

    }

}
