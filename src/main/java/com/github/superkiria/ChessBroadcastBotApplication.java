package com.github.superkiria;

import com.github.superkiria.cbbot.ChessBroadcastBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class ChessBroadcastBotApplication {

	public static void main(String[] args) throws Exception {
//		ConfigurableApplicationContext context = SpringApplication.run(ChessBroadcastBotApplication.class, args);
//		ChessBroadcastBot bot = context.getBean(ChessBroadcastBot.class);
		SpringApplicationBuilder spring = new SpringApplicationBuilder(ChessBroadcastBotApplication.class);
		ConfigurableApplicationContext context = spring.application().run();
		System.out.println("join");
		context.getBean(LichessStreamDumper.class).start("DYcygfVK");
//		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//		telegramBotsApi.registerBot(bot);
//		context.getBean(MessageSender.class).start();
//		context.getBean(PgnDispatcher.class).start();
	}

}
