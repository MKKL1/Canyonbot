package com.mkkl.canyonbot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.Getter;
import reactor.core.publisher.Mono;

@Getter
public abstract class BotCommand {
    private final ApplicationCommandRequest commandRequest;
    private final CommandErrorHandler errorHandler;

    protected BotCommand(ApplicationCommandRequest commandRequest, CommandErrorHandler errorHandler) {
        this.commandRequest = commandRequest;
        this.errorHandler = errorHandler;
    }

    public abstract Mono<Void> execute(ChatInputInteractionEvent event);
}
