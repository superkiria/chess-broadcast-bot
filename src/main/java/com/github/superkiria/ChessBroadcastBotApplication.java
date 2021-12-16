package com.github.superkiria;

import com.github.superkiria.lichess.stub.LichessStreamDumper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ChessBroadcastBotApplication {

	public static void main(String[] args) throws Exception {
//		ConfigurableApplicationContext context = SpringApplication.run(ChessBroadcastBotApplication.class, args);
//		ChessBroadcastBot bot = context.getBean(ChessBroadcastBot.class);
		SpringApplicationBuilder spring = new SpringApplicationBuilder(ChessBroadcastBotApplication.class).profiles("staging");
		ConfigurableApplicationContext context = spring.application().run();
//		context.getBean(LichessStreamDumper.class).start("DYcygfVK");
//		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//		telegramBotsApi.registerBot(bot);
//		context.getBean(MessageSender.class).start();
//		context.getBean(PgnDispatcher.class).start();
	}

}
