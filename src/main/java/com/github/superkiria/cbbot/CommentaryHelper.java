package com.github.superkiria.cbbot;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.bhlangonijr.chesslib.move.MoveList;

public class CommentaryHelper {

    public static String parseTime(String comment) {
        if (comment == null) {
            return "";
        }
        int start = comment.lastIndexOf("%clk");
        if (start < 0) {
            return "";
        }
        int end = comment.indexOf(']', start);
        String result = comment.substring(start + 5, end);
        if (result.startsWith("0:")) {
            result = result.substring(2);
        }
        return "| " + result;
    }

    public static String parseEval(String comment) {
        if (comment == null) {
            return "";
        }
        int start = comment.lastIndexOf("%eval");
        if (start < 0) {
            return "";
        }
        int end = comment.indexOf(']', start);
        return "| " + comment.substring(start + 6, end);
    }

    public static String moveFromMovesList(Game game, int move) {
        String comment = game.getCommentary().get(move);
        int moveNumber = move / 2 + move % 2;
        String forBlackMove = move % 2 == 0 ? ".." : "";
        return moveNumber + "." + forBlackMove + " " + game.getHalfMoves().get(move - 1).getSan() + " " + CommentaryHelper.parseTime(comment) + " " + CommentaryHelper.parseEval(comment);

    }

}
