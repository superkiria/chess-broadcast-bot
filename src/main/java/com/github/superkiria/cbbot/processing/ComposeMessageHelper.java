package com.github.superkiria.cbbot.processing;

import com.github.superkiria.cbbot.lichess.model.LichessEvent;
import com.github.superkiria.cbbot.lichess.model.LichessRound;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ComposeMessageHelper {

    public static String eventInfo(LichessEvent event) {
        Stream<LichessRound> sorted = event.getRounds().stream().sorted(Comparator.comparing(LichessRound::getStartsAt));
        List<String> texts = new ArrayList<>();
        texts.add(event.getTour().getName());
        sorted.forEach(round -> texts.add(round.getName()
                + " | " + round.getStartsAt()
                + " | " + (round.getOngoing() != null && round.getOngoing() ? "ongoing" : "")
                + (round.getFinished() != null && round.getFinished() ? "finished" : "")));
        return String.join("\n", texts);
    }

    public static InlineKeyboardMarkup eventSubscribeButton(String eventId) {
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder markup = InlineKeyboardMarkup.builder();
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .callbackData("subscribe:" + eventId)
                .text("Подписаться на этот турнир")
                .build();
        markup.keyboardRow(Collections.singletonList(button));
        return markup.build();
    }

}
