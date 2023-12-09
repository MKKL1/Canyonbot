package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RegisterCommand
public class StopCommand extends BotCommand {
    private final MusicPlayerManager musicPlayerManager;

    public StopCommand(MusicPlayerManager musicPlayerManager, DefaultErrorHandler errorHandler) {
        super(ApplicationCommandRequest.builder()
                .name("stop")
                .description("Stops playing music")
                .build(), errorHandler);
        this.musicPlayerManager = musicPlayerManager;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.getInteraction()
                .getGuild()
                .flatMap(guild -> musicPlayerManager.getPlayer(guild)
                        .orElseThrow(() -> new IllegalStateException("Player of guild not found"))
                        .getTrackScheduler()
                        .stop()
                        .onErrorResume(throwable -> {
                            if (throwable instanceof IllegalStateException)
                                return event.reply("Player is already stopped");
                            return Mono.empty();
                        })
                        .then(event.reply("Stopped playing music and cleared the queue"))
                ).publishOn(Schedulers.boundedElastic());
    }
}
