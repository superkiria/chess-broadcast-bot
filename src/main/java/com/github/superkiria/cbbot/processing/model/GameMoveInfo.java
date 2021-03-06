package com.github.superkiria.cbbot.processing.model;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameMoveInfo {

    private String pgn;
    private String round;
    private String white;
    private String black;
    private boolean hasMoves;
    private Game game;
    private GameResult gameResult;
    private int halfMove;

}
