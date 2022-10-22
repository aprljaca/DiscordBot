package com.DiscordBot.listener;

import lombok.Data;
import org.javacord.api.DiscordApi;
import org.springframework.stereotype.Component;

@Component
@Data
public class PlayerManager {

    private DiscordApi api;

}
