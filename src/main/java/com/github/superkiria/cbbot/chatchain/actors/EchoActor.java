package com.github.superkiria.cbbot.chatchain.actors;

import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.cbbot.chatchain.ChatActor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class EchoActor implements ChatActor {

    @Override
    public void act(ChatContext context) {
        Update update = context.getUpdate();
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(update.getMessage().getText());
    //            List<InlineKeyboardButton> buttons = new ArrayList<>();
    //            for (int i = 0; i < 64; i++) {
    //                buttons.add(InlineKeyboardButton.builder().text("" + i).callbackData("" + i).build());
    //            }
    //            InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboardRow(buttons).build();
            List<KeyboardRow> keyboardRows = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                KeyboardRow keyboardRow = new KeyboardRow();
                for (int j = 0; j < 3; j++) {
                    keyboardRow.add(i + "-" + j + "seventh+");
                }
                keyboardRows.add(keyboardRow);
            }
            ReplyKeyboardMarkup markup = ReplyKeyboardMarkup.builder().keyboard(keyboardRows).oneTimeKeyboard(true).build();
            message.setReplyMarkup(markup);
            context.setResponse(message);
        }
    }
}
