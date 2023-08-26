package com.mkkl.canyonbot.command;

import discord4j.rest.RestClient;
import discord4j.rest.interaction.GuildCommandRegistrar;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CommandRegistry
{
    private final List<CommandList> registeredCommands;
    private final HashMap<String, BotCommand> commandsByName;

    public CommandRegistry() {
        this.registeredCommands = new ArrayList<>();
        this.commandsByName = new HashMap<>();
    }

    public void AddCommands(CommandList commandList) {
        registeredCommands.add(commandList);
        for(BotCommand botCommand : commandList) {
            commandsByName.put(botCommand.getCommandRequest().name(), botCommand);
        }
    }

    public GuildCommandRegistrar AddCommands(RestClient restClient, CommandList commandList) {
        AddCommands(commandList);
        return GuildCommandRegistrar.create(restClient, commandList.GetCommandRequests());
    }

    public GuildCommandRegistrar GetGuildCommandRegistrar(RestClient restClient) {
        return GuildCommandRegistrar.create(restClient, registeredCommands
                .stream()
                .map(CommandList::GetCommandRequests)
                .flatMap(Collection::stream)
                .toList());
    }

    public Optional<BotCommand> GetCommandByName(String name) {
        return Optional.ofNullable(commandsByName.get(name));
    }
}
