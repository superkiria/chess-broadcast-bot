package com.github.superkiria.cbbot.chatchain;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

@Builder
@Data
public class ChatContext {
    private String chatId;
    private Update update;
    private SendMessage response;
    private SendPhoto sendPhoto;
}
