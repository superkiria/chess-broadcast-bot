package com.github.superkiria.cbbot.processing.message;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.superkiria.cbbot.sending.SentDataKeeper;
import com.github.superkiria.cbbot.processing.model.GameKey;
import com.github.superkiria.cbbot.model.MarkedCaption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.github.superkiria.cbbot.processing.message.CommentaryHelper.moveFromMovesList;
import static com.github.superkiria.cbbot.processing.message.CommentaryHelper.timeFromMovesList;

@Component
public class MoveMarkedCaptionConstructor {

    private final SentDataKeeper sentDataKeeper;

    @Autowired
    public MoveMarkedCaptionConstructor(SentDataKeeper sentDataKeeper) {
        this.sentDataKeeper = sentDataKeeper;
    }

    public MarkedCaption makeMarkedCaptionFromGame(Game game, GameKey gameKey, int halfMove) {
        return makeMarkedCaptionFromGame(game, gameKey, halfMove, false);
    }

    public MarkedCaption makeMarkedCaptionFromGame(Game game, GameKey gameKey, int halfMove, boolean makeItShort) {
        CaptionMarkupBuilder constructor = new CaptionMarkupBuilder();
        if (!game.getResult().equals(GameResult.ONGOING) && game.getHalfMoves().size() - 1 == halfMove) {
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
        if (halfMove >= 1) {
            constructor.addString(moveFromMovesList(game, halfMove - 1) + "\n", "code");
        }
        if (halfMove >= 0) {
            constructor.addString(moveFromMovesList(game, halfMove) + "\n", "code");
        }
        if (halfMove >= 1) {
            String time;
            if (halfMove % 2 == 0) {
                time = timeFromMovesList(game, halfMove) + " ⏱ " + timeFromMovesList(game, halfMove - 1) + "\n";
            } else {
                time = timeFromMovesList(game, halfMove - 1) + " ⏱ " + timeFromMovesList(game, halfMove) + "\n";
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
