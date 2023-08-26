package com.mkkl.canyonbot.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.Getter;
import org.reactivestreams.Publisher;

public abstract class BotCommand {
    @Getter
    private final ApplicationCommandRequest commandRequest;

    protected BotCommand(ApplicationCommandRequest commandRequest) {
        this.commandRequest = commandRequest;
    }

    abstract Publisher<?> execute(ChatInputInteractionEvent event);
}
