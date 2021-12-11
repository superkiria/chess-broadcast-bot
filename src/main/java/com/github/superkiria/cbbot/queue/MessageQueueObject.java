package com.github.superkiria.cbbot.queue;

import com.github.superkiria.chatchain.ChatContext;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageQueueObject {

    private String chatId;
    private ChatContext context;

}
