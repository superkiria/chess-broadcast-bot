package com.github.superkiria.cbbot;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
