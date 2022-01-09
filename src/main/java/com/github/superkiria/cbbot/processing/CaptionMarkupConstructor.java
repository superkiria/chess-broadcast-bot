package com.github.superkiria.cbbot.processing;

import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.ArrayList;
import java.util.List;

public class CaptionMarkupConstructor {

    private final StringBuilder caption = new StringBuilder();
    private final List<MessageEntity> entities = new ArrayList<>();
    private int offset = 0;

    public void addStringLn(String s, String type) {
        addString(s, type);
        addLineBreak();
    }

    public void addString(String s, String type) {
        caption.append(s);
        if (type != null) {
            entities.add(MessageEntity.builder()
                    .type(type)
                    .text(s)
                    .offset(offset)
                    .length(s.length())
                    .build()
            );
        }
        offset += s.length();
    }

    public void addLink(String text, String url) {
        caption.append(text);
        entities.add(MessageEntity.builder()
                .type("text_link")
                .text(text)
                .url(url)
                .offset(offset)
                .length(text.length())
                .build());
        offset += text.length();
    }

    public void addLineBreak() {
        caption.append("\n");
        offset += "\n".length();
    }

    public String getCaption() {
        return caption.toString();
    }

    public List<MessageEntity> getEntities() {
        return entities;
    }

}
