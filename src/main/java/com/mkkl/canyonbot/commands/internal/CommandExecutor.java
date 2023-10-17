package com.mkkl.canyonbot.commands.internal;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.CommandRegistry;
import discord4j.core.DiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
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
        this.register().subscribe(); // TODO move?
    }

    public Mono<Void> register() {
        return discordClient.withGateway(gateway -> gateway.on(new ReactiveEventAdapter() {
            @Override
            public Publisher<?> onChatInputInteraction(ChatInputInteractionEvent event) {
                Optional<BotCommand> optionalCommand = commandRegistry.getCommandByName(event.getCommandName());
                if (optionalCommand.isEmpty()) {
                    return event.reply("Command not found");//TODO localize
                }
                return optionalCommand.get()
                        .execute(event);
            }
        }));
    }
}
