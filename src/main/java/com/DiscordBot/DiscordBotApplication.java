package com.DiscordBot;

import com.DiscordBot.listener.PlayerManager;
import org.javacord.api.DiscordApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscordBotApplication {

    public DiscordBotApplication(PlayerManager playerManager, DiscordApi api) {
        playerManager.setApi(api);
    }

    public static void main(String[] args) {
        SpringApplication.run(DiscordBotApplication.class, args);
    }

}