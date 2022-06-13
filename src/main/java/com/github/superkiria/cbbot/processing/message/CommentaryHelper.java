package com.github.superkiria.cbbot.processing.message;

import com.github.bhlangonijr.chesslib.game.Game;

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
        return result;
    }

    public static String parseEval(String comment) {
        if (comment == null) {
            return "";
        }
        int start = comment.lastIndexOf("%eval");
        int end = comment.indexOf(']', start);
        if (start < 0 || end < 0 || end <= start) {
            return "";
        }
        return "| " + comment.substring(start + 6, end);
    }

    public static String moveFromMovesList(Game game, int move) {
        if (game.getHalfMoves().size() == 0) {
            return "";
        }
        int moveNumber = move / 2 + 1;
        String forBlackMove = move % 2 == 0 ? "" : "..";
        String result = moveNumber + "." + forBlackMove + " " + game.getHalfMoves().get(move).getSan();
        if (game.getCommentary() != null) {
            String comment = game.getCommentary().get(move);
            result = result + " " + CommentaryHelper.parseEval(comment);
        }
        return result;
    }

    public static String timeFromMovesList(Game game, int halfMove) {
        if (game.getHalfMoves().size() == 0 || game.getCommentary() == null) {
            return "";
        }
        String comment = game.getCommentary().get(halfMove + 1);
        return CommentaryHelper.parseTime(comment);
    }

}
