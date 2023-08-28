package com.mkkl.canyonbot.command;

import com.mkkl.canyonbot.command.registrar.CommandRegistrar;
import com.mkkl.canyonbot.command.registrar.CommandRegistrarFactory;
import discord4j.rest.RestClient;
import discord4j.rest.interaction.GuildCommandRegistrar;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CommandRegistry
{
    private final List<CommandList> registeredCommands;
    private final HashMap<String, BotCommand> commandsByName;
    private final CommandRegistrarFactory commandRegistrarFactory;

    public CommandRegistry(CommandRegistrarFactory commandRegistrarFactory) {
        this.commandRegistrarFactory = commandRegistrarFactory;
        this.registeredCommands = new ArrayList<>();
        this.commandsByName = new HashMap<>();
    }

    public void AddCommands(CommandList commandList) {
        registeredCommands.add(commandList);
        for(BotCommand botCommand : commandList) {
            commandsByName.put(botCommand.getCommandRequest().name(), botCommand);
        }
    }

    public CommandRegistrar AddCommandsWithRegistrar(CommandList commandList) {
        AddCommands(commandList);
        return commandRegistrarFactory.create(commandList.GetCommandRequests());
    }

    public CommandRegistrar GetCommandRegistrar() {
        return commandRegistrarFactory.create(registeredCommands
                .stream()
                .map(CommandList::GetCommandRequests)
                .flatMap(Collection::stream)
                .toList());
    }

    public Optional<BotCommand> GetCommandByName(String name) {
        return Optional.ofNullable(commandsByName.get(name));
    }
}
