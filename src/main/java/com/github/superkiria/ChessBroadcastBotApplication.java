package com.github.superkiria;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ChessBroadcastBotApplication {

	public static void main(String[] args) throws Exception {
		SpringApplicationBuilder spring = new SpringApplicationBuilder(ChessBroadcastBotApplication.class).profiles();
		spring.application().run();
	}

}
