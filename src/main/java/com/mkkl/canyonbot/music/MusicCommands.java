package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.CommandRegistry;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
