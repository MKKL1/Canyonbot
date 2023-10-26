package com.mkkl.canyonbot.commands;

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import reactor.core.publisher.Mono;

public interface AutoCompleteCommand {
    Mono<Void> autoComplete(ChatInputAutoCompleteEvent event);
}
