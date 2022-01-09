package com.github.superkiria.cbbot.sending.model;

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
