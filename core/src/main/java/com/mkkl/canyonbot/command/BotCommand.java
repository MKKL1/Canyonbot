package com.mkkl.canyonbot.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.Getter;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public abstract class BotCommand {
    @Getter
    private final ApplicationCommandRequest commandRequest;

    protected BotCommand(ApplicationCommandRequest commandRequest) {
        this.commandRequest = commandRequest;
    }

    public abstract Mono<Void> execute(ChatInputInteractionEvent event);
}
