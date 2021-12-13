package com.github.superkiria.cbbot.broadcast.model;

import com.github.bhlangonijr.chesslib.game.Game;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExtractedGame {

    private String pgn;
    private String gameId;
    private boolean hasMoves;
    private Game game;

}
