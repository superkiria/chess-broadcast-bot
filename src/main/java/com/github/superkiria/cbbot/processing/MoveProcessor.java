package com.github.superkiria.cbbot.processing;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.bhlangonijr.chesslib.pgn.PgnProperty;
import com.github.superkiria.cbbot.admin.SubscriptionManager;
import com.github.superkiria.cbbot.processing.model.GameKey;
import com.github.superkiria.cbbot.processing.model.GameMoveInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.bhlangonijr.chesslib.pgn.PgnProperty.parsePgnProperty;
import static com.github.superkiria.cbbot.processing.message.GameKeyHelper.gameKeyFromGame;
import static com.github.superkiria.cbbot.processing.message.PgnHelper.makeGameFromPgn;

@Component
public class MoveProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(MoveProcessor.class);

    private final PgnProcessor pgnProcessor;
    private final SubscriptionManager subscriptionManager;

    private Map<GameKey, Integer> lastPublishedMoves = new HashMap<>();

    @Autowired
    public MoveProcessor(PgnProcessor pgnProcessor, SubscriptionManager subscriptionManager) {
        this.pgnProcessor = pgnProcessor;
        this.subscriptionManager = subscriptionManager;
    }

    public void reset() {
        lastPublishedMoves = new HashMap<>();
    }

    public List<GameMoveInfo> getMovesForNextGame() throws InterruptedException {
        List<String> pgn = this.pgnProcessor.waitAndPreparePgn();
        List<GameMoveInfo> result = new ArrayList<>();
        try {
            Game lastMoveGame = makeGameFromPgn(pgn);
            GameKey key = gameKeyFromGame(subscriptionManager.getCurrentSubscription(), lastMoveGame);
            if (!lastPublishedMoves.containsKey(key)) {
                lastPublishedMoves.put(key, Math.max(lastMoveGame.getHalfMoves().size() - 10, 0));
            }
            for (int i = lastPublishedMoves.get(key) + 1; i < lastMoveGame.getHalfMoves().size(); i++) {
                lastPublishedMoves.put(key, i);
                Game gameForAMove = makeGameFromPgn(pgn, i);
                result.add(GameMoveInfo.builder()
                        .game(gameForAMove)
                        .round(subscriptionManager.getCurrentSubscription())
                        .white(gameForAMove.getWhitePlayer().getName())
                        .black(gameForAMove.getBlackPlayer().getName())
                        .halfMove(i)
                        .build()
                );
                LOG.debug("Game extracted");
            }
            if (result.size() == 0 && lastPublishedMoves.get(key) < lastMoveGame.getHalfMoves().size()) {
                result.add(GameMoveInfo.builder()
                        .game(lastMoveGame)
                        .round(subscriptionManager.getCurrentSubscription())
                        .white(lastMoveGame.getWhitePlayer().getName())
                        .black(lastMoveGame.getBlackPlayer().getName())
                        .halfMove(lastMoveGame.getHalfMoves().size() - 1)
                        .build());
            }
            if (lastMoveGame.getResult() != GameResult.ONGOING) {
                lastPublishedMoves.put(key, lastPublishedMoves.get(key) + 1);
            }
        } catch (Throwable e) {
            LOG.warn("Game extraction failed", e);
            LOG.debug(String.join("\n", pgn));
            result.add(fallbackGameExtraction(pgn));
        }
        return result;
    }

    private GameMoveInfo fallbackGameExtraction(List<String> pgn) {
        GameMoveInfo.GameMoveInfoBuilder builder = GameMoveInfo.builder();
        builder.round(subscriptionManager.getCurrentSubscription());
        for (String str : pgn) {
            if (PgnProperty.isProperty(str)) {
                PgnProperty pgnProperty = parsePgnProperty(str);
                if (pgnProperty.name.equalsIgnoreCase("white")) {
                    builder.white(pgnProperty.value);
                }
                if (pgnProperty.name.equalsIgnoreCase("black")) {
                    builder.black(pgnProperty.value);
                }
                continue;
            }
            if (str.endsWith("*")) {
                builder.gameResult(GameResult.ONGOING);
                continue;
            }
            if (str.endsWith("1/2-1/2")) {
                builder.gameResult(GameResult.DRAW);
                continue;
            }
            if (str.endsWith("1-0")) {
                builder.gameResult(GameResult.WHITE_WON);
                continue;
            }
            if (str.endsWith("0-1")) {
                builder.gameResult(GameResult.BLACK_WON);
            }
        }
        return builder.build();
    }

}
