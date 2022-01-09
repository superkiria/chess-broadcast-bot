package com.github.superkiria.cbbot.sending.model;

import com.github.bhlangonijr.chesslib.game.Game;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExtractedGame {

    private String pgn;
    private String round;
    private String white;
    private String black;
    private boolean hasMoves;
    private Game game;

}
