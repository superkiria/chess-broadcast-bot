package com.github.superkiria.cbbot.model;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.List;

@Data
@Builder
public class MarkedCaption {

    private String caption;
    private List<MessageEntity> entities;

}
