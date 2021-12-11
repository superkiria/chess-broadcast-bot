package com.github.superkiria.cbbot;

import com.github.superkiria.cbbot.channel.MovePublisher;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class MoveConsumer implements Consumer<String> {

    private final MovePublisher publisher;

    @Autowired
    public MoveConsumer(MovePublisher publisher) {
        this.publisher = publisher;
    }

    @SneakyThrows
    @Override
    public void accept(String pgnPart) {
        publisher.addPartOfPgn(pgnPart);
    }
}
