package com.mkkl.canyonbot.commands.internal;

import com.mkkl.canyonbot.commands.AutoCompleteCommand;
import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.CommandRegistry;
import discord4j.core.DiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.interaction.AutoCompleteInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class CommandExecutor {
    private final DiscordClient discordClient;
    private final CommandRegistry commandRegistry;

    @Autowired
    public CommandExecutor(DiscordClient discordClient, CommandRegistry commandRegistry) {
        this.discordClient = discordClient;
        this.commandRegistry = commandRegistry;
        this.register()
                .subscribe(); // TODO move?
    }

    public Mono<Void> register() {
        return discordClient.withGateway(gateway -> gateway.on(ChatInputInteractionEvent.class, event -> {
            //TODO localize
            return commandRegistry.getCommandByName(event.getCommandName())
                .map(botCommand -> botCommand.execute(event)
                        .onErrorResume(throwable -> botCommand.getErrorHandler().handle(throwable, event)))
                .orElseGet(() -> event.reply("Command not found"));
            }))
            .and(discordClient.withGateway(gateway -> gateway.on(ChatInputAutoCompleteEvent.class, event -> {
                Optional<AutoCompleteCommand> optionalCommand = commandRegistry.getAutoCompleteCommandByName(event.getCommandName());
                if (optionalCommand.isEmpty()) {
                    return Mono.empty();
                }
                return optionalCommand.get()
                        .autoComplete(event);
            })));
    }
}
