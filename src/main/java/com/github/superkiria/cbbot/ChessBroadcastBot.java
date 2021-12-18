package com.github.superkiria.cbbot;

import com.github.superkiria.cbbot.chatchain.ActorsChain;
import com.github.superkiria.cbbot.chatchain.ChatContext;
import com.github.superkiria.props.SecretProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ChessBroadcastBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(ChessBroadcastBot.class);

    private final SecretProps secretProps;
    private final ActorsChain chain;

    @Autowired
    public ChessBroadcastBot(SecretProps secretProps, ActorsChain chain) {
        this.secretProps = secretProps;
        this.chain = chain;
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
        LOG.info("Message received: {}", update);
        ChatContext context = ChatContext.builder().update(update).build();
        chain.startWithContext(context);
    }

}
