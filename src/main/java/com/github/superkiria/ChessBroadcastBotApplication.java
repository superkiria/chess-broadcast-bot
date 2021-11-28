package com.github.superkiria;

import com.github.superkiria.cbbot.ChessBroadcastBot;
import com.github.superkiria.cbbot.MoveConsumer;
import com.github.superkiria.cbbot.MovePublisher;
import com.github.superkiria.lichess.BroadcastConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.function.Consumer;

@SpringBootApplication
public class ChessBroadcastBotApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(ChessBroadcastBotApplication.class, args);
		ChessBroadcastBot bot = context.getBean(ChessBroadcastBot.class);
		BroadcastConsumer broadcastConsumer = context.getBean(BroadcastConsumer.class);
		MovePublisher publisher = new MovePublisher(bot);
		Consumer<String> consumer = new MoveConsumer(publisher);
		publisher.startPublishing();
		broadcastConsumer.pgnsForRound("dyeyegeX").subscribe(consumer);
	}

}
