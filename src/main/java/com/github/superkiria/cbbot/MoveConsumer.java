package com.github.superkiria.cbbot;

import lombok.SneakyThrows;

import java.util.function.Consumer;

public class MoveConsumer implements Consumer<String> {

    private final MovePublisher publisher;

    public MoveConsumer(MovePublisher publisher) {
        this.publisher = publisher;
    }

    @SneakyThrows
    @Override
    public void accept(String pgnPart) {
        publisher.addPartOfPgn(pgnPart);
    }
}
