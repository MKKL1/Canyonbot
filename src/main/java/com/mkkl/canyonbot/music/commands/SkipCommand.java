package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.music.MusicPlayerManager;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

@RegisterCommand
public class SkipCommand extends BotCommand {
    private final MusicPlayerManager musicPlayerManager;

    public SkipCommand(MusicPlayerManager musicPlayerManager, DefaultErrorHandler errorHandler) {
        super(ApplicationCommandRequest.builder()
                .name("skip")
                .description("Skips the current track")
                .build(), errorHandler);
        this.musicPlayerManager = musicPlayerManager;
    }

    //TODO ERROR skip doesn't respond with message but works
    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.getInteraction()
                .getGuild()
                .flatMap(guild -> musicPlayerManager.getPlayer(guild)
                        .orElseThrow(() -> new IllegalStateException("Player of guild not found"))
                        .getTrackScheduler()
                        .skip()
                        .flatMap(trackQueueElement -> event.reply("Skipping " + trackQueueElement.getAudioTrack().getInfo().title))
                        .onErrorResume(throwable -> {
                            if (throwable instanceof IllegalStateException)
                                return event.reply("Nothing to skip");
                            return event.reply("Error while skipping track");
                        })
                );
    }
}
