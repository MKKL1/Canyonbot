package com.mkkl.canyonbot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public interface CommandErrorHandler {
    Mono<Void> handle(Throwable throwable, ChatInputInteractionEvent event);
}
