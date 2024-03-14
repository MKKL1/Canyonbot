package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.commands.exceptions.UserResponseMessage;
import com.mkkl.canyonbot.music.exceptions.GuildMusicBotNotCreated;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RegisterCommand
public class SkipCommand extends BotCommand {
    private final LinkContextRegistry linkContextRegistry;

    public SkipCommand(DefaultErrorHandler errorHandler, LinkContextRegistry linkContextRegistry) {
        super(ApplicationCommandRequest.builder()
                .name("skip")
                .description("Skips the current track")
                .build(), errorHandler);
        this.linkContextRegistry = linkContextRegistry;
    }

    //TODO ERROR skip doesn't respond with message but works
    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.getInteraction()
                .getGuild()
                .flatMap(guild -> {
                    if (!linkContextRegistry.isCached(guild))
                        return Mono.error(new GuildMusicBotNotCreated(guild));
                    return linkContextRegistry.getCached(guild)
                            .get()
                            .getTrackScheduler()
                            .skip()
                            .switchIfEmpty(Mono.error(new UserResponseMessage("Nothing to skip")))
                            .flatMap(track -> event.reply("Skipping " + track.getTrack().getInfo().getTitle()));
                });
    }
}
