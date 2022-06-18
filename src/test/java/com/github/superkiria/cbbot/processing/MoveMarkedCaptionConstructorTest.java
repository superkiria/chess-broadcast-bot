package com.github.superkiria.cbbot.processing;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.GameLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class MoveMarkedCaptionConstructorTest {

    @Test
    public void test_001() {
        List<String> buffer = List.of(
                "[Event \"Tata Steel Chess Masters 2022\"]",
                "[Site \"Wijk aan Zee, Netherlands\"]",
                "[Date \"2022.01.22\"]",
                "[Round \"7.4\"]",
                "[White \"Esipenko, Andrey\"]",
                "[Black \"Shankland, Sam\"]",
                "[Result \"*\"]",
                "[WhiteElo \"2714\"]",
                "[BlackElo \"2708\"]",
                "[UTCDate \"2022.01.22\"]",
                "[UTCTime \"13:08:27\"]",
                "[Variant \"Standard\"]",
                "[ECO \"B48\"]",
                "[Opening \"Sicilian Defense: Taimanov Variation, Bastrikov Variation\"]",
                "1. e4 { [%clk 1:40:55] } 1... c5 { [%clk 1:40:48] } 2. Nf3 { [%clk 1:40:43] } 2... e6 { [%clk 1:41:13] } 3. d4 { [%clk 1:40:18] } 3... cxd4 { [%clk 1:41:39] } 4. Nxd4 { [%clk 1:40:45] } 4... Nc6 { [%clk 1:42:05] } 5. Nc3 { [%clk 1:40:59] } 5... Qc7 { [%clk 1:42:29] } 6. Be3 { [%clk 1:41:09] } 6... a6 { [%clk 1:42:53] } 7. g4 { [%clk 1:41:04] } 7... b5 { [%clk 1:43:03] } 8. Nxc6 { [%clk 1:41:04] } 8... Qxc6 { [%clk 1:43:28] } 9. Qd2 { [%clk 1:41:24] } 9... b4 { [%clk 1:43:48] } 10. Ne2 { [%clk 1:41:47] } 10... Qxe4 { [%clk 1:44:11] } 11. Rg1 { [%clk 1:42:13] } 11... Qc4 { [%clk 1:44:34] } 12. Ng3 { [%clk 1:41:16] } 12... Qc7 { [%clk 1:44:51] } 13. O-O-O { [%clk 1:41:25] } 13... Ne7 { [%clk 1:45:15] } 14. Nh5 { [%clk 1:40:57] } 14... Nd5 { [%clk 1:45:17] } 15. Bd4 { [%clk 1:40:50] } 15... Rg8 { [%clk 1:45:34] } 16. Qd3 { [%clk 1:40:52] } 16... g6 { [%clk 1:45:53] } 17. Bg2 { [%clk 1:39:56] } 17... gxh5 { [%clk 1:45:59] } 18. Bxd5 { [%clk 1:38:07] } 18... Bb7 { [%clk 1:46:25] } 19. Bxb7 { [%clk 1:30:50] } 19... Qxb7 { [%clk 1:46:46] } 20. gxh5 { [%clk 1:08:31] } 20... Rxg1 { [%clk 1:46:47] } 21. Rxg1 { [%clk 1:08:58] } 21... Qd5 { [%clk 1:47:07] } 22. b3 { [%clk 1:01:27] } 22... f5 { [%clk 1:44:57] } 23. Qe3 { [%clk 0:50:50] } 23... Qe4 { [%clk 1:25:25] } 24. Qd2 { [%clk 0:47:18] } 24... Rc8 { [%clk 1:23:06] } 25. Kb1 { [%clk 0:44:58] } 25... f4 { [%clk 1:18:04] } 26. Qd1 { [%clk 0:37:57] } 26... d6 { [%clk 1:11:21] } 27. f3 { [%clk 0:32:20] } 27... Qd5 { [%clk 1:03:12] } 28. Qd3 { [%clk 0:29:52] } 28... Rc7 { [%clk 1:01:40] } 29. Re1 { [%clk 0:23:49] } 29... Re7 { [%clk 0:55:08] } 30. Re4 { [%clk 0:24:04] } 30... e5 { [%clk 0:42:25] } 31. Qxa6 { [%clk 0:24:22] } 31... Kf7 { [%clk 0:42:23] } 32. Bb2 { [%clk 0:19:35] } 32... Qd1+ { [%clk 0:40:24] } 33. Bc1 { [%clk 0:20:01] } 33... d5 { [%clk 0:40:19] } 34. Re2 { [%clk 0:18:54] } 34... Rc7 { [%clk 0:36:31] } 35. Rf2 { [%clk 0:17:23] } 35... d4 { [%clk 0:26:20] } 36. Qb6 { [%clk 0:13:14] } 36... Rc3 { [%clk 0:25:03] } 37. Qb7+ { [%clk 0:11:54] } 37... Be7 { [%clk 0:24:48] } 38. Qe4 { [%clk 0:08:44] } 38... Ke6 { [%clk 0:21:31] } 39. Qxh7 { [%clk 0:08:14] } 39... Qg1 { [%clk 0:20:45] } 40. Qg6+ { [%clk 0:02:28] } 40... Qxg6 { [%clk 0:21:08] } 41. hxg6 { [%clk 0:02:55] } 41... Kf6 { [%clk 0:12:47] } 42. Bd2 { [%clk 0:01:03] } 42... Rc8 { [%clk 0:11:27] } 43. Rg2 { [%clk 0:41:02] } 43... Kg7 { [%clk 1:00:22] } 44. Re2 { [%clk 0:31:38] } 44... Bd6 { [%clk 0:52:12] } 45. h4 { [%clk 0:31:27] } 45... Rh8 { [%clk 0:52:16] } 46. Be1 { [%clk 0:30:31] } 46... Kxg6 { [%clk 0:52:26] } 47. Kc1 { [%clk 0:30:08] } 47... Ra8 { [%clk 0:45:31] } *"
        );
        Game game = GameLoader.loadNextGame(buffer.listIterator());
        Board board = new Board();
        game.setBoard(board);
        MoveList moves = game.getHalfMoves();
        int halfMoveNumber = 3;
        if (halfMoveNumber >= moves.size()) {
            return;
        }
        Iterator var3 = moves.iterator();
        System.out.println(moves.size() > 0 ? String.valueOf(moves.getLast()) : null);
        System.out.println(board.getFen());
        for (int i = 0; i < halfMoveNumber; i++) {
            Move move = (Move) var3.next();
            board.doMove(move);
            System.out.println(board.getFen() + " " + move.toString());
        }
    }

}
