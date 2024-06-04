package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.DiscordCommand;
import com.mkkl.canyonbot.commands.exceptions.BotInternalException;
import com.mkkl.canyonbot.db.TrackDBService;
import com.mkkl.canyonbot.db.entity.TrackEntity;
import com.mkkl.canyonbot.music.services.PlayerService;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

import java.util.List;

@DiscordCommand
public class HistoryCommand extends BotCommand {
    public static final int ELEMENTS_PER_PAGE = 20; //TODO this may be command option
    private final GatewayDiscordClient gateway;
    private final TrackDBService trackDBService;

    protected HistoryCommand(DefaultErrorHandler errorHandler, GatewayDiscordClient gateway, TrackDBService trackDBService) {
        super(ApplicationCommandRequest.builder()
                .name("history")
                .description("Shows last played songs")
                .build(), errorHandler);
        this.gateway = gateway;
        this.trackDBService = trackDBService;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return Mono.defer(() -> Mono.justOrEmpty(event.getInteraction().getGuildId()))
                .switchIfEmpty(Mono.error(new BotInternalException("GuildId was undefined")))
                .map(Snowflake::asLong)
                .flatMap(guild -> {
                    List<TrackEntity> tracks = trackDBService.getLastPlayedTracks(guild);
                    StringBuilder stringBuilder = new StringBuilder();
                    tracks.forEach(track -> stringBuilder.append(track.getTitle())
                            .append(" ")
                            .append(track.getUri())
                            .append('\n'));
                    return event.reply(InteractionApplicationCommandCallbackSpec.builder()
                                    .addEmbed(EmbedCreateSpec.builder()
                                            .description(stringBuilder.toString())
                                            .build())
                            .build());
                })

                .then();
    }
}