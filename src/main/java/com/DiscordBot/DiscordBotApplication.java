package com.DiscordBot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;



@SpringBootApplication
public class DiscordBotApplication {

	@Value("${token}")
	private String token;


	public static void main(String[] args) {
		SpringApplication.run(DiscordBotApplication.class, args);
	}

	@Bean
	@ConfigurationProperties("discord-api")
	public DiscordApi discordApi() {
		DiscordApi api = new DiscordApiBuilder()
				.setAllIntents()
				.setToken(token)
				.login()
				.join();


		api.addMessageCreateListener(event -> {
			if (event.getMessageContent().equalsIgnoreCase(".ping")) {
				event.getChannel().sendMessage(".pong");
			}
		});

		api.addMessageCreateListener(event -> {
			if (event.getMessageContent().equalsIgnoreCase(".ping")) {
				ServerVoiceChannel channel = event.getMessageAuthor().getConnectedVoiceChannel().get();
				channel.connect().thenAccept(audioConnection -> {
					// Do stuff
				}).exceptionally(e -> {
					// Failed to connect to voice channel (no permissions?)
					e.printStackTrace();
					return null;
				});
			}
		});

		return api;
	}

}