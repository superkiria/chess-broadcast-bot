package com.github.superkiria.cbbot.main;

import com.github.superkiria.cbbot.actions.ActorsChain;
import com.github.superkiria.cbbot.props.SecretProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;

@Component
public class ChessBroadcastBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(ChessBroadcastBot.class);

    @Autowired
    Environment env;

    private final SecretProps secretProps;
    private final ActorsChain chain;
    private final TelegramBotsApi telegramBotsApi;

    @Autowired
    public ChessBroadcastBot(SecretProps secretProps, ActorsChain chain, TelegramBotsApi telegramBotsApi) {
        this.secretProps = secretProps;
        this.chain = chain;
        this.telegramBotsApi = telegramBotsApi;
    }

    @EventListener
    public void start(ApplicationReadyEvent event) throws TelegramApiException {
        if (Arrays.stream(env.getActiveProfiles()).noneMatch(o -> o.equalsIgnoreCase("test"))) {
            telegramBotsApi.registerBot(this);
            LOG.info("Bot started...");
        }
    }

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
        LOG.info("Message received: {}", update.getMessage() != null ? update.getMessage().getFrom() : (update.getChannelPost() != null) ? "Channel post" : "Unknown");
        ChatContext context = ChatContext.builder().update(update).build();
        chain.startWithContext(context);
    }

}
