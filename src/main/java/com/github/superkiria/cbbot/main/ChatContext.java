package com.github.superkiria.cbbot.main;

import com.github.superkiria.cbbot.sending.model.GameKey;
import com.github.superkiria.cbbot.sending.model.MarkedCaption;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.List;

@Builder
@Data
public class ChatContext {

    private final static Logger LOG = LoggerFactory.getLogger(ChatContext.class);

    private boolean skip;
    private String chatId;
    private Integer messageId;
    private Integer forwardedReplyMessageId;
    private Update update;
    private String response;
    private InputStream inputStream;
    private String round;
    private String white;
    private String black;
    private InlineKeyboardMarkup inlineKeyboardMarkup;
    private Integer color;
    private GameKey key;
    private List<MessageEntity> entities;
    private String stickerId;
    private String fileId;
    private MarkedCaption shortMarkedCaption;

    public Message call(TelegramLongPollingBot bot) throws IllegalStateException, TelegramApiException {
        if (stickerId != null) {
            bot.execute(makeSendSticker());
            return null;
        }
        if (forwardedReplyMessageId != null && fileId != null) {
            bot.execute(makeSendExistingPhoto());
            return null;
        }
        if (messageId != null && inputStream != null) {
             return (Message) bot.execute(makeEditMessageMedia());
        }
        if (this.getInputStream() != null) {
            try {
                return bot.execute(makeSendPhoto());
            } catch (Throwable e) {
                LOG.error(e.getMessage() + "\nChatContext:\n" + this);
            }
        }
        if (this.getResponse() != null) {
            try {
                return bot.execute(makeSendMessage());
            } catch (Throwable e) {
                LOG.error(e.getMessage() + "\nChatContext:\n" + this);
            }
        }
        LOG.info("No valid data to send " + this);
        return null;
    }

    private SendMessage makeSendMessage() {
        return SendMessage.builder()
                .chatId(chatId)
                .text(response)
                .entities(entities)
                .replyToMessageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .disableWebPagePreview(true)
                .build();
    }

    private SendPhoto makeSendPhoto() {
        return SendPhoto.builder()
                .chatId(chatId)
                .caption(response)
                .captionEntities(entities)
                .replyToMessageId(messageId)
                .photo(new InputFile(inputStream, "file.png"))
                .build();
    }

    private SendPhoto makeSendExistingPhoto() {
        return SendPhoto.builder()
                .chatId(chatId)
                .caption(shortMarkedCaption.getCaption())
                .captionEntities(shortMarkedCaption.getEntities())
                .replyToMessageId(forwardedReplyMessageId)
                .photo(new InputFile(fileId))
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
                .media(InputMediaPhoto.builder().caption(response).entities(entities).media("attach://file.png").mediaName("file.png").newMediaStream(inputStream).isNewMedia(true).build())
                .build();
    }

    private SendSticker makeSendSticker() {
        return SendSticker.builder()
                .chatId(chatId)
                .sticker(new InputFile(stickerId))
                .disableNotification(true)
                .build();
    }

}
