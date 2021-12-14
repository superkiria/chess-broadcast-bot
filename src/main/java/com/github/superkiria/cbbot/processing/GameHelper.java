package com.github.superkiria.cbbot.processing;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.GameLoader;

import java.util.List;

public class GameHelper {

    public static Game makeGameFromPgn(List<String> buffer) {
        Game game = GameLoader.loadNextGame(buffer.listIterator());
        game.setBoard(new Board());
        game.gotoLast();
        return game;
    }

}
