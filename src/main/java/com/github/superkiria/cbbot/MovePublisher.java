package com.github.superkiria.cbbot;

import com.github.superkiria.chess.pgn.PgnTools;
import com.github.superkiria.chess.svg.SvgBoardBuilder;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.github.superkiria.chess.svg.SvgUtils.saveDocumentToPngByteBuffer;

public class MovePublisher {

    private final ChessBroadcastBot bot;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    public MovePublisher(ChessBroadcastBot bot) {
        this.bot = bot;
    }

    public void addPartOfPgn(String part) {
        queue.add(part);
    }

    public void startPublishing() {
        Thread thread = new Thread() {
            @SneakyThrows
            public void run() {
                while (true) {
                    try {
                        List<String> buffer = new ArrayList<>();
                        String part = "";
                        while (!part.startsWith("1.")) {
                            part = queue.take();
                            buffer.add(part);
                        }
                        buffer.add(part);
                        String pgn = String.join("\n", buffer);
                        String fen = PgnTools.convertPgnToFen(pgn);
                        SvgBoardBuilder builder = new SvgBoardBuilder();
                        builder.setFen(fen);
                        builder.init();
                        ByteArrayOutputStream baos = saveDocumentToPngByteBuffer(builder.getDocument());
                        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
                        bot.sendTextToChannel(String.valueOf(queue.size()));
                        bot.sendPhotoToChannel(inputStream, "file");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    } finally {
                        Thread.sleep(10000);
                    }
                }
            }
        };
        thread.start();
    }

}
