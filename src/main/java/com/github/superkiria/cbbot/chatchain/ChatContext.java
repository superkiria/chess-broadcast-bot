package com.github.superkiria.cbbot.chatchain;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;

@Builder
@Data
public class ChatContext {
    private String chatId;
    private Integer messageId;
    private Update update;
    private String response;
    private InputStream inputStream;
    private String round;
    private String white;
    private String black;
    private InlineKeyboardMarkup inlineKeyboardMarkup;

    public Message call(TelegramLongPollingBot bot) throws IllegalStateException, TelegramApiException {
        if (messageId != null && inputStream != null) {
             bot.execute(makeEditMessageMedia());
             return null;
        }
        if (this.getInputStream() != null) {
            return bot.execute(makeSendPhoto());
        }
        if (this.getResponse() != null) {
            return bot.execute(makeSendMessage());
        }
        throw new IllegalStateException("No data to send");
    }

    private SendMessage makeSendMessage() {
        return SendMessage.builder()
                .chatId(chatId)
                .text(response)
                .replyToMessageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    private SendPhoto makeSendPhoto() {
        return SendPhoto.builder()
                .chatId(chatId)
                .caption(response)
                .replyToMessageId(messageId)
                .photo(new InputFile(inputStream, "file.png"))
                .build();
    }

    private EditMessageCaption makeEditMessageCaption() {
        return EditMessageCaption.builder()
                .chatId(chatId)
                .messageId(messageId)
                .caption(response)
                .build();
    }

    private EditMessageMedia makeEditMessageMedia() {
        return EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .media(InputMediaPhoto.builder().caption(response).media("attach://file.png").mediaName("file.png").newMediaStream(inputStream).isNewMedia(true).build())
                .build();
    }

}