package com.github.superkiria.cbbot.processing;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.bhlangonijr.chesslib.pgn.GameLoader;
import com.github.superkiria.cbbot.sending.keepers.SentDataKeeper;
import com.github.superkiria.cbbot.sending.model.GameKey;
import com.github.superkiria.cbbot.sending.model.MarkedCaption;
import com.github.superkiria.chess.svg.SvgBoardBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import static com.github.superkiria.cbbot.processing.CommentaryHelper.moveFromMovesList;
import static com.github.superkiria.cbbot.processing.CommentaryHelper.timeFromMovesList;
import static com.github.superkiria.chess.svg.SvgUtils.saveDocumentToPngByteBuffer;

@Component
public class GameHelper {

    private final SentDataKeeper sentDataKeeper;

    @Autowired
    public GameHelper(SentDataKeeper sentDataKeeper) {
        this.sentDataKeeper = sentDataKeeper;
    }

    public static Game makeGameFromPgn(List<String> buffer) {
        Game game = GameLoader.loadNextGame(buffer.listIterator());
        game.setBoard(new Board());
        game.gotoLast();
        return game;
    }

    public static Game makeGameFromPgn(List<String> buffer, int halfMove) {
        Game game = GameLoader.loadNextGame(buffer.listIterator());
        game.setBoard(new Board());
        game.gotoMove(game.getHalfMoves(), halfMove);
        return game;
    }

    public static InputStream makePictureFromGame(Game game, Integer color, int halfMove) throws Exception {
        SvgBoardBuilder builder = new SvgBoardBuilder();
        builder.setFen(game.getBoard().getFen());
        builder.setLastMoveInNotation(game.getHalfMoves().get(halfMove - 1).toString());
        builder.init(color);
        ByteArrayOutputStream baos = saveDocumentToPngByteBuffer(builder.getDocument());
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public MarkedCaption makeMarkedCaptionFromGame(Game game, GameKey gameKey, int halfMove) {
        return makeMarkedCaptionFromGame(game, gameKey, halfMove, false);
    }

    public MarkedCaption makeMarkedCaptionFromGame(Game game, GameKey gameKey, int halfMove, boolean makeItShort) {
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
        if (halfMove > 1) {
            constructor.addString(moveFromMovesList(game, halfMove - 1) + "\n", "code");
        }
        if (halfMove > 0) {
            constructor.addString(moveFromMovesList(game, halfMove) + "\n", "code");
        }
        if (halfMove > 1) {
            String time;
            if (halfMove % 2 == 0) {
                time = timeFromMovesList(game, halfMove - 1) + " ⏱ " + timeFromMovesList(game, halfMove) + "\n";
            } else {
                time = timeFromMovesList(game, halfMove) + " ⏱ " + timeFromMovesList(game, halfMove - 1) + "\n";
            }
            if (time.length() > " ⏱ \n".length()) {
                constructor.addString(time, null);
            }
        }

        if (!makeItShort) {
            constructor.addString(game.getWhitePlayer().getName() + " - " + game.getBlackPlayer().getName() + "\n", "bold");
        }

        if (halfMove > 0 && game.getOpening() != null && game.getOpening().strip().length() > 6) {
            if (!makeItShort || !game.getOpening().equals(sentDataKeeper.getOpening(gameKey))) {
                constructor.addString(game.getOpening() + "\n", "italic");
            }
        }

        if (!makeItShort) {
            if (game.getRound() != null
                    && game.getRound().getEvent() != null
                    && game.getRound().getEvent().getSite() != null
                    && game.getRound().getEvent().getSite().trim().startsWith("http")
                    && game.getRound().getEvent().getSite().contains("lichess.org")) {
                constructor.addLink("check on lichess", game.getRound().getEvent().getSite());
            }
        }

        return MarkedCaption.builder().caption(constructor.getCaption()).entities(constructor.getEntities()).build();
    }

}
