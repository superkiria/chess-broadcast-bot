package com.github.superkiria.cbbot.processing.message;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.GameLoader;

import java.util.List;

public class PgnHelper {

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

}
