package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.commands.CommandRegistry;
import com.mkkl.canyonbot.music.commands.HelloCommand;
import com.mkkl.canyonbot.music.commands.PlayCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.DiscordClient;
import org.springframework.stereotype.Component;

@Component
public class MusicCommands {
    private final DiscordClient discordClient;
    private final CommandRegistry commandRegistry;
    private final AudioPlayerManager audioPlayerManager;
    public MusicCommands(DiscordClient discordClient, CommandRegistry commandRegistry, AudioPlayerManager audioPlayerManager) {
        this.discordClient = discordClient;
        this.commandRegistry = commandRegistry;
        this.audioPlayerManager = audioPlayerManager;

        commandRegistry.add(new HelloCommand(), new PlayCommand(audioPlayerManager));
        commandRegistry.add();
        commandRegistry.getCommandRegistrar().registerCommands().blockFirst();
    }
}
