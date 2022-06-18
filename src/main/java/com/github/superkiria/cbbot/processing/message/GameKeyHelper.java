package com.github.superkiria.cbbot.processing.message;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.superkiria.cbbot.processing.model.GameKey;
import com.github.superkiria.cbbot.processing.model.GameMoveInfo;

public class GameKeyHelper {

    public static GameKey gameKeyFromExtractedGame(GameMoveInfo gameMoveInfo) {
        return GameKey.builder()
                .round(gameMoveInfo.getRound())
                .white(gameMoveInfo.getWhite())
                .black(gameMoveInfo.getBlack())
                .build();
    }

    public static GameKey gameKeyFromGame(String round, Game game) {
        return GameKey.builder()
                .round(round)
                .white(game.getWhitePlayer().getName())
                .black(game.getBlackPlayer().getName())
                .build();
    }

}
