package com.github.superkiria;

import com.github.superkiria.cbbot.main.ChessBroadcastBot;
import com.github.superkiria.cbbot.sending.MessageSender;
import com.github.superkiria.cbbot.processing.PgnDispatcher;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class ChessBroadcastBotApplication {

	public static void main(String[] args) throws Exception {
		SpringApplicationBuilder spring = new SpringApplicationBuilder(ChessBroadcastBotApplication.class).profiles();
		ConfigurableApplicationContext context = spring.application().run();
		ChessBroadcastBot bot = context.getBean(ChessBroadcastBot.class);
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		telegramBotsApi.registerBot(bot);
		context.getBean(MessageSender.class).start();
		context.getBean(PgnDispatcher.class).start();
	}

}
