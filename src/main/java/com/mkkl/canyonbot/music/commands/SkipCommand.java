package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.DiscordCommand;
import com.mkkl.canyonbot.commands.exceptions.BotExternalException;
import com.mkkl.canyonbot.commands.exceptions.BotInternalException;
import com.mkkl.canyonbot.music.exceptions.GuildMusicBotNotCreated;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import com.mkkl.canyonbot.music.services.PlayerService;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

@DiscordCommand
public class SkipCommand extends BotCommand {
    private final PlayerService playerService;

    public SkipCommand(DefaultErrorHandler errorHandler, PlayerService playerService) {
        super(ApplicationCommandRequest.builder()
                .name("skip")
                .description("Skips the current track")
                .build(), errorHandler);
        this.playerService = playerService;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return Mono.defer(() -> Mono.justOrEmpty(event.getInteraction().getGuildId()))
                .switchIfEmpty(Mono.error(new BotInternalException("GuildId was undefined")))
                .map(Snowflake::asLong)
                .flatMap(playerService::skipTrack)
                .flatMap(track -> event.reply("Skipping " + track.getTitle()));
    }
}
