package com.github.superkiria.cbbbot;

import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.github.superkiria.cbbot.CommentaryHelper.moveFromMovesList;
import static com.github.superkiria.cbbot.GameHelper.makeGameFromPgn;

public class MoveConsumerTest {

    String input = "" +
            "[Event \"Game 9: Ian Nepomniachtchi - Magnus Carlsen\"]\n" +
            "[Site \"https://lichess.org/study/mOnQPtG7/cuCfhlgA\"]\n" +
            "[Date \"2021.12.07\"]\n" +
            "[White \"Ian Nepomniachtchi\"]\n" +
            "[Black \"Magnus Carlsen\"]\n" +
            "[Result \"1/2-1/2\"]\n" +
            "[WhiteElo \"2782\"]\n" +
            "[WhiteTitle \"GM\"]\n" +
            "[BlackElo \"2855\"]\n" +
            "[BlackTitle \"GM\"]\n" +
            "[UTCDate \"2021.11.27\"]\n" +
            "[UTCTime \"09:10:13\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"?\"]\n" +
            "[Opening \"?\"]\n" +
            "[Annotator \"https://lichess.org/@/cFlour\"]\n" +
            "\n" +
            "1/2-1/2\n" +
//            "\n" +
//            "\n" +
//            "[Event \"Game 9: Ian Nepomniachtchi - Magnus Carlsen\"]\n" +
//            "[Site \"https://lichess.org/study/mOnQPtG7/cuCfhlgA\"]\n" +
//            "[Date \"2021.12.07\"]\n" +
//            "[White \"Ian Nepomniachtchi\"]\n" +
//            "[Black \"Magnus Carlsen\"]\n" +
//            "[Result \"*\"]\n" +
//            "[WhiteElo \"2782\"]\n" +
//            "[WhiteTitle \"GM\"]\n" +
//            "[BlackElo \"2855\"]\n" +
//            "[BlackTitle \"GM\"]\n" +
//            "[UTCDate \"2021.11.27\"]\n" +
//            "[UTCTime \"09:10:13\"]\n" +
//            "[Variant \"Standard\"]\n" +
//            "[ECO \"?\"]\n" +
//            "[Opening \"?\"]\n" +
//            "[Annotator \"https://lichess.org/@/cFlour\"]\n" +
//            "\n" +
//            "*\n" +
//            "\n" +
//            "\n" +
//            "[Event \"Game 9: Ian Nepomniachtchi - Magnus Carlsen\"]\n" +
//            "[Site \"https://lichess.org/study/mOnQPtG7/cuCfhlgA\"]\n" +
//            "[Date \"2021.12.07\"]\n" +
//            "[White \"Ian Nepomniachtchi\"]\n" +
//            "[Black \"Magnus Carlsen\"]\n" +
//            "[Result \"*\"]\n" +
//            "[WhiteElo \"2782\"]\n" +
//            "[WhiteTitle \"GM\"]\n" +
//            "[BlackElo \"2855\"]\n" +
//            "[BlackTitle \"GM\"]\n" +
//            "[UTCDate \"2021.11.27\"]\n" +
//            "[UTCTime \"09:10:13\"]\n" +
//            "[Variant \"Standard\"]\n" +
//            "[ECO \"A10\"]\n" +
//            "[Opening \"English Opening\"]\n" +
//            "[Annotator \"https://lichess.org/@/cFlour\"]\n" +
//            "\n" +
//            "1. c4 { [%clk 2:00:00] } *\n" +
            "";

    @Test
    public void test() {
        Game game = makeGameFromPgn(Arrays.stream(input.split("\n")).collect(Collectors.toList()));
        System.out.println(Long.toBinaryString(game.getBoard().getBitboard(Side.WHITE)));
        System.out.println(Long.toBinaryString(game.getBoard().getBitboard(Side.BLACK)));
        System.out.println(Long.toBinaryString(game.getBoard().getBitboard()));
        System.out.println(game.getBoard().getPositionId());
        System.out.println(moveFromMovesList(game, 1));
    }

}