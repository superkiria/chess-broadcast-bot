package com.github.superkiria.cbbot.actions.actors;

import com.github.superkiria.cbbot.actions.ChatActor;
import com.github.superkiria.cbbot.main.ChatContext;
import com.github.superkiria.cbbot.sending.keepers.SentDataKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CatchForwardsActor implements ChatActor {

    private final SentDataKeeper sentDataKeeper;

    private final static Logger LOG = LoggerFactory.getLogger(CatchForwardsActor.class);

    @Value("${telegram.group.chatId}")
    private String groupId;

    @Autowired
    public CatchForwardsActor(SentDataKeeper sentDataKeeper) {
        this.sentDataKeeper = sentDataKeeper;
    }

    @Override
    public void act(ChatContext context) {
        if (context.getUpdate() == null || context.getUpdate().getMessage() == null) {
            return;
        }
        if (context.getUpdate().getMessage().getChat().getId().toString().equals(groupId)) {
            if (context.getUpdate().getMessage().getIsAutomaticForward() != null
                    && context.getUpdate().getMessage().getIsAutomaticForward()) {
                sentDataKeeper.putForward(
                        context.getUpdate().getMessage().getForwardFromMessageId(),
                        context.getUpdate().getMessage().getMessageId()
                );
                LOG.info("Forward registered from {} to {}",
                        context.getUpdate().getMessage().getForwardFromMessageId(),
                        context.getUpdate().getMessage().getMessageId());
            }
        }


    }

}
