package com.mkkl.canyonbot.command;

import discord4j.core.DiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class CommandExecutor {
    private final DiscordClient discordClient;
    private final CommandRegistry commandRegistry;

    public CommandExecutor(DiscordClient discordClient, CommandRegistry commandRegistry) {
        this.discordClient = discordClient;
        this.commandRegistry = commandRegistry;
    }

    public Mono<Void> register() {
        return discordClient.withGateway(gateway -> gateway.on(new ReactiveEventAdapter() {
            @Override
            public Publisher<?> onChatInputInteraction(ChatInputInteractionEvent event) {
                Optional<BotCommand> optionalCommand = commandRegistry.GetCommandByName(event.getCommandName());
                if (optionalCommand.isEmpty()) {
                    return event.reply("Command not found");//TODO localize
                }
                return optionalCommand.get()
                        .execute(event);
            }
        }));
    }
}
