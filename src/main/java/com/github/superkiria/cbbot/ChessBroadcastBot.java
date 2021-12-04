package com.github.superkiria.cbbot;

import com.github.superkiria.props.SecretProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;

@Component
public class ChessBroadcastBot extends TelegramLongPollingBot {

    private static String CHAT_ID = "-1001694568044";

    @Autowired
    private SecretProps secretProps;

    @Override
    public String getBotUsername() {
        return secretProps.getName();
    }

    @Override
    public String getBotToken() {
        return secretProps.getKey();
    }

    @Override
    public void onUpdateReceived(Update update) {
    }

    public void sendTextToChannel(String text) {
        SendMessage message = SendMessage.builder()
                .text(text)
                .chatId(CHAT_ID)
                .build();
        try {
            this.execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendPhotoToChannel(InputStream stream, String fileName, String caption) {
        SendPhoto message = SendPhoto
                .builder()
                .caption(caption)
                .photo(new InputFile(stream, fileName))
                .chatId(CHAT_ID)
                .build();
        try {
            this.execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
