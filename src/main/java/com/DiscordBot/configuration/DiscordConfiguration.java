package com.DiscordBot.configuration;

import com.DiscordBot.listener.MessageListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DiscordConfiguration {
    @Autowired
    private Environment env;

    @Bean
    public DiscordApi discordApi(MessageListener messageListener) {
        String token = env.getProperty("TOKEN");
        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .setAllIntents()
                .login()
                .join();

        api.addMessageCreateListener(messageListener);
        return api;
    }
}
