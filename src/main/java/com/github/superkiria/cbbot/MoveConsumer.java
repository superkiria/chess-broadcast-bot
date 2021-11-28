package com.github.superkiria.cbbot;

import com.github.superkiria.chess.pgn.PgnTools;
import com.github.superkiria.chess.svg.SvgBoardBuilder;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.github.superkiria.chess.svg.SvgUtils.saveDocumentToPngByteBuffer;

public class MoveConsumer implements Consumer<String> {

    private final MovePublisher publisher;
    private List<String> buffer = new ArrayList<>();

    public MoveConsumer(MovePublisher publisher) {
        this.publisher = publisher;
    }

    @SneakyThrows
    @Override
    public void accept(String pgnPart) {
        publisher.addPartOfPgn(pgnPart);
    }
}
