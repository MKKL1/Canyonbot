package com.mkkl.canyonbot;

import com.mkkl.canyonbot.command.CommandExecutor;
import com.mkkl.canyonbot.command.CommandRegistry;
import discord4j.core.DiscordClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CanyonBot {

    @Getter
    private final DiscordClient discordClient;
    @Getter
    private final CommandRegistry commandRegistry;
    @Getter
    private final CommandExecutor commandExecutor;

    @Autowired
    public CanyonBot(DiscordClient discordClient, CommandRegistry commandRegistry, CommandExecutor commandExecutor) {
        this.discordClient = discordClient;
        this.commandRegistry = commandRegistry;
        this.commandExecutor = commandExecutor;
        commandExecutor.register().subscribe();
    }

}
