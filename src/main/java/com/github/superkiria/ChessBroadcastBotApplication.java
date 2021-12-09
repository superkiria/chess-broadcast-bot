package com.github.superkiria;

import com.github.superkiria.cbbot.ChessBroadcastBot;
import com.github.superkiria.cbbot.MoveConsumer;
import com.github.superkiria.cbbot.MovePublisher;
import com.github.superkiria.lichess.BroadcastConsumer;
import com.github.superkiria.lichess.model.LichessEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.function.Consumer;

@SpringBootApplication
public class ChessBroadcastBotApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(ChessBroadcastBotApplication.class, args);
		ChessBroadcastBot bot = context.getBean(ChessBroadcastBot.class);
		BroadcastConsumer broadcastConsumer = context.getBean(BroadcastConsumer.class);
		Consumer<String> consumer = context.getBean(MoveConsumer.class);
		context.getBean(MovePublisher.class).startPublishing();
		broadcastConsumer.pgnsForRound(args[0]).subscribe(consumer);
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		telegramBotsApi.registerBot(bot);
	}

}
