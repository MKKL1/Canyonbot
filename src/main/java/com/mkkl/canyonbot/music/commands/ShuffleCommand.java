package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.commands.exceptions.BotExternalException;
import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.music.messages.generators.QueueMessage;
import com.mkkl.canyonbot.music.player.LinkContext;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Guild;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@RegisterCommand
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
