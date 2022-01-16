package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.main.ChatContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogMessageActor implements ChatActor {

    private final static Logger LOG = LoggerFactory.getLogger(LogMessageActor.class);


    @Override
    public void act(ChatContext context) {
        LOG.debug(String.valueOf(context.getUpdate()));
        try {
            LOG.info(context.getUpdate().getMessage().getSticker().getFileId());
        } catch (Throwable ignored) {

        }
    }
}
