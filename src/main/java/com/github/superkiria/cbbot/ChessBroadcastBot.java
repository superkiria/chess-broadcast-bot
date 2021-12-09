package com.github.superkiria.cbbot;

import com.github.superkiria.props.SecretProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;

@Component
public class ChessBroadcastBot extends TelegramLongPollingBot {

    private static final String CHAT_ID = "-1001694568044";
    private final static Logger LOG = LoggerFactory.getLogger(ChessBroadcastBot.class);

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
        LOG.info("Message received: {} {}", update.getMessage().getChat().getUserName(), update.getMessage().getChat().getId());
    }

    public void sendTextToChannel(String text) {
        SendMessage message = SendMessage.builder()
                .text(text)
                .chatId(CHAT_ID)
                .build();
        try {
            this.execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            LOG.error("On sending message to " + CHAT_ID + ": " + text, e);
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
            LOG.error("On sending photo to " + CHAT_ID + " with caption: " + caption, e);
        }
    }

}
