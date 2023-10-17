package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.commands.CommandRegistry;
import com.mkkl.canyonbot.music.commands.HelloCommand;
import discord4j.core.DiscordClient;
import org.springframework.stereotype.Component;

@Component
public class MusicCommands {
    private final DiscordClient discordClient;
    private final CommandRegistry commandRegistry;
    public MusicCommands(DiscordClient discordClient, CommandRegistry commandRegistry) {
        this.discordClient = discordClient;
        this.commandRegistry = commandRegistry;

        commandRegistry.add(new HelloCommand());
        commandRegistry.getCommandRegistrar().registerCommands().blockFirst();
    }
}
