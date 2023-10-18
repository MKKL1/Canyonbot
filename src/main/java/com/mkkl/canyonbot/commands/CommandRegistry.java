package com.mkkl.canyonbot.commands;

import com.mkkl.canyonbot.commands.registrar.CommandRegistrar;
import com.mkkl.canyonbot.commands.registrar.CommandRegistrarFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CommandRegistry {
    private final HashMap<String, BotCommand> commandsByName;
    private final CommandRegistrarFactory commandRegistrarFactory;
    public CommandRegistry(CommandRegistrarFactory commandRegistrarFactory, ApplicationContext context) {
        this.commandRegistrarFactory = commandRegistrarFactory;
        this.commandsByName = new HashMap<>();
        Map<String,Object> commandBeans = context.getBeansWithAnnotation(RegisterCommand.class);
        for (Map.Entry<String, Object> entry : commandBeans.entrySet()) {
            if (entry.getValue() instanceof BotCommand) {
                add((BotCommand) entry.getValue());
            }
        }
        getCommandRegistrar().registerCommands().subscribe();//TODO not sure if it should be here
    }

    public void add(List<BotCommand> botCommands) {
        for (BotCommand command : botCommands) {
            commandsByName.put(command.getCommandRequest().name(), command);
        }
    }

    public void add(BotCommand... botCommands) {
        for (BotCommand command : botCommands) {
            commandsByName.put(command.getCommandRequest().name(), command);
        }
    }

    public void add(BotCommand botCommand) {
        commandsByName.put(botCommand.getCommandRequest().name(), botCommand);
    }

    public CommandRegistrar getCommandRegistrar() {
        return commandRegistrarFactory.create(commandsByName.values().stream().map(BotCommand::getCommandRequest).toList());
    }

    public Optional<BotCommand> getCommandByName(String commandName) {
        return Optional.ofNullable(commandsByName.get(commandName));
    }
}
