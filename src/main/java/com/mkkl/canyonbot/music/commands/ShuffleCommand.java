package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.DiscordCommand;
import com.mkkl.canyonbot.commands.exceptions.BotExternalException;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

@DiscordCommand
public class ShuffleCommand extends BotCommand {
    private final LinkContextRegistry linkContextRegistry;

    protected ShuffleCommand(DefaultErrorHandler errorHandler, LinkContextRegistry linkContextRegistry) {
        super(ApplicationCommandRequest.builder()
                .name("shuffle")
                .description("Randomizes the queue")
                .build(), errorHandler);
        this.linkContextRegistry = linkContextRegistry;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.getInteraction().getGuild()
                .flatMap(guild -> Mono.justOrEmpty(linkContextRegistry.getCached(guild.getId().asLong())))
                .switchIfEmpty(Mono.error(new BotExternalException("Queue is empty")))
                .flatMap(linkContext -> {
                    linkContext.getTrackQueue().shuffle();
                    return event.reply("Shuffled the queue");
                });
    }
}
