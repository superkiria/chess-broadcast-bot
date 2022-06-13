package com.github.superkiria.cbbot.processing.message;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.superkiria.chess.svg.SvgBoardBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.github.superkiria.chess.svg.SvgUtils.saveDocumentToPngByteBuffer;

public class PictureHelper {

    public static InputStream makePictureFromGame(Game game, Integer color, int halfMove) throws Exception {
        SvgBoardBuilder builder = new SvgBoardBuilder();
        builder.setFen(game.getBoard().getFen());
        builder.setLastMoveInNotation(game.getHalfMoves().get(halfMove).toString());
        builder.init(color);
        ByteArrayOutputStream baos = saveDocumentToPngByteBuffer(builder.getDocument());
        return new ByteArrayInputStream(baos.toByteArray());
    }

}
