package com.github.superkiria.cbbot.main;

import com.github.superkiria.cbbot.sending.model.GameKey;
import com.github.superkiria.cbbot.sending.model.MarkedCaption;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.Date;

@Builder
@Data
public class ChatContext implements Comparable<ChatContext> {

    private final static Logger LOG = LoggerFactory.getLogger(ChatContext.class);

    private boolean skip;
    private Update update;
    private String chatId;
    private MarkedCaption markedCaption;
    private MarkedCaption shortMarkedCaption;
    private Integer replyMessageId;
    private Integer forwardedReplyMessageId;
    private InputStream inputStream;
    private InlineKeyboardMarkup inlineKeyboardMarkup;
    private String stickerId;
    private String fileId;
    private Long scheduledTime;
    private GameKey key;
    private Integer color;
    private String opening;

    public Message call(TelegramLongPollingBot bot) throws IllegalStateException, TelegramApiException {
        if (stickerId != null) {
            executeWithTry(bot, makeSendSticker());
            return null;
        }
        if (forwardedReplyMessageId != null && fileId != null) {
            executeWithTry(bot, makeSendExistingPhoto());
            return null;
        }
        if (replyMessageId != null && inputStream != null) {
             return executeWithTry(bot, makeEditMessageMedia());
        }
        if (this.getInputStream() != null) {
            return executeWithTry(bot, makeSendPhoto());
        }
        if (this.getMarkedCaption() != null) {
            executeWithTry(bot, makeSendMessage());
        }
        LOG.debug("No valid data to send " + this);
        return null;
    }

    private Message executeWithTry(TelegramLongPollingBot bot, BotApiMethod method) {
        try {
            return (Message) bot.execute(method);
        } catch (Throwable e) {
            LOG.error("\nChatContext:\n" + this, e);
        }
        return null;
    }

    private Message executeWithTry(TelegramLongPollingBot bot, SendPhoto method) {
        try {
            return bot.execute(method);
        } catch (Throwable e) {
            LOG.error("\nChatContext:\n" + this, e);
        }
        return null;
    }

    private Message executeWithTry(TelegramLongPollingBot bot, SendSticker method) {
        try {
            return bot.execute(method);
        } catch (Throwable e) {
            LOG.error("\nChatContext:\n" + this, e);
        }
        return null;
    }

    private Message executeWithTry(TelegramLongPollingBot bot, EditMessageMedia method) {
        try {
            return (Message) bot.execute(method);
        } catch (Throwable e) {
            LOG.error("\nChatContext:\n" + this, e);
        }
        return null;
    }


    private SendMessage makeSendMessage() {
        return SendMessage.builder()
                .chatId(chatId)
                .text(markedCaption.getCaption())
                .entities(markedCaption.getEntities())
                .replyToMessageId(replyMessageId)
                .replyMarkup(inlineKeyboardMarkup)
                .disableWebPagePreview(true)
                .build();
    }

    private SendPhoto makeSendPhoto() {
        return SendPhoto.builder()
                .chatId(chatId)
                .caption(markedCaption.getCaption())
                .captionEntities(markedCaption.getEntities())
                .replyToMessageId(replyMessageId)
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
                .messageId(replyMessageId)
                .caption(markedCaption.getCaption())
                .build();
    }

    private EditMessageMedia makeEditMessageMedia() {
        return EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(replyMessageId)
                .media(InputMediaPhoto.builder()
                        .caption(markedCaption.getCaption())
                        .entities(markedCaption.getEntities())
                        .media("attach://file.png")
                        .mediaName("file.png")
                        .newMediaStream(inputStream)
                        .isNewMedia(true).build())
                .build();
    }

    private SendSticker makeSendSticker() {
        return SendSticker.builder()
                .chatId(chatId)
                .sticker(new InputFile(stickerId))
                .disableNotification(true)
                .build();
    }

    @Override
    public int compareTo(ChatContext chatContext) {
        if (scheduledTime == null) {
            scheduledTime = new Date().getTime();
        }
        if (chatContext.getScheduledTime() == null) {
            chatContext.setScheduledTime(new Date().getTime());
        }
        return scheduledTime.compareTo(chatContext.getScheduledTime());
    }
}
