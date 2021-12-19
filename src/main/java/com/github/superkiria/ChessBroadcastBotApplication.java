package com.github.superkiria;

import com.github.superkiria.cbbot.ChessBroadcastBot;
import com.github.superkiria.cbbot.incoming.lichess.SubscriptionManager;
import com.github.superkiria.cbbot.outgoing.MessageSender;
import com.github.superkiria.cbbot.outgoing.PgnDispatcher;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class ChessBroadcastBotApplication {

	public static void main(String[] args) throws Exception {
//		ConfigurableApplicationContext context = SpringApplication.run(ChessBroadcastBotApplication.class, args);
		SpringApplicationBuilder spring = new SpringApplicationBuilder(ChessBroadcastBotApplication.class).profiles();
		ConfigurableApplicationContext context = spring.application().run();
		ChessBroadcastBot bot = context.getBean(ChessBroadcastBot.class);
//		context.getBean(LichessStreamDumper.class).start("DYcygfVK");
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		telegramBotsApi.registerBot(bot);
		context.getBean(MessageSender.class).start();
		context.getBean(PgnDispatcher.class).start();
		context.getBean(SubscriptionManager.class).start();
	}

}
