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
    private final static int attempts = 2;

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

    public Message call(TelegramLongPollingBot bot) throws IllegalStateException, TelegramApiException, InterruptedException {
        if (stickerId != null) {
            executeWithRetry(bot, makeSendSticker(), attempts);
            return null;
        }
        if (forwardedReplyMessageId != null && fileId != null) {
            executeWithRetry(bot, makeSendExistingPhoto(), attempts);
            return null;
        }
        if (replyMessageId != null && inputStream != null) {
             return executeWithRetry(bot, makeEditMessageMedia(), attempts);
        }
        if (this.getInputStream() != null) {
            return executeWithRetry(bot, makeSendPhoto(), attempts);
        }
        if (this.getMarkedCaption() != null) {
            executeWithRetry(bot, makeSendMessage(), attempts);
        }
        LOG.warn("No valid data to send " + this);
        return null;
    }

    private Message executeWithRetry(TelegramLongPollingBot bot, Object method, int retriesLeft) throws InterruptedException {
        try {
            if (method instanceof EditMessageMedia) {
                return (Message) bot.execute((EditMessageMedia) method);
            }
            if (method instanceof SendPhoto) {
                return  bot.execute((SendPhoto) method);
            }
            if (method instanceof BotApiMethod) {
                return (Message) bot.execute((BotApiMethod) method);
            }
            if (method instanceof SendSticker) {
                return bot.execute((SendSticker) method);
            }
        } catch (Throwable e) {
            LOG.error("Message sending failed", e);
            LOG.error("Context:\n{}", this);
            LOG.error("Method:\n{}", method);
            if (retriesLeft > 0) {
                retriesLeft--;
                LOG.info("Retrying, retries left {}", retriesLeft);
                Thread.sleep(1000);
                executeWithRetry(bot, method, retriesLeft);
            } else {
                LOG.error("Retries are left - won't retry");
            }
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
