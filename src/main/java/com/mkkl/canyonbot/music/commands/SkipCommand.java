package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.music.MusicPlayerManager;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RegisterCommand
public class SkipCommand extends BotCommand {
    private final MusicPlayerManager musicPlayerManager;

    public SkipCommand(MusicPlayerManager musicPlayerManager) {
        super(ApplicationCommandRequest.builder()
                .name("skip")
                .description("Skips the current track")
                .build());
        this.musicPlayerManager = musicPlayerManager;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.reply("Skipped the current track")
                .then(event.getInteraction()
                        .getGuild()
                        .flatMap(guild -> musicPlayerManager.getPlayer(guild)
                                .getTrackScheduler()
                                .skip()).publishOn(Schedulers.boundedElastic())
                );
    }
}
