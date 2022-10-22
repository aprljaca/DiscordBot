package com.DiscordBot.command;

import org.javacord.api.event.message.MessageCreateEvent;

public abstract class Command {
    public abstract void run(MessageCreateEvent event);
}
