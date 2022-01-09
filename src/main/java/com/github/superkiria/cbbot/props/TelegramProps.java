package com.github.superkiria.cbbot.props;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TelegramProps {

    @Value("${telegram.admin.chatId}")
    private String adminChatId;

}
