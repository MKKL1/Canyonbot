package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.music.services.GuildTrackSchedulerService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

@RegisterCommand
public class SkipCommand extends BotCommand {
    private final GuildTrackSchedulerService trackSchedulerService;

    public SkipCommand(GuildTrackSchedulerService trackSchedulerService, DefaultErrorHandler errorHandler) {
        super(ApplicationCommandRequest.builder()
                .name("skip")
                .description("Skips the current track")
                .build(), errorHandler);
        this.trackSchedulerService = trackSchedulerService;
    }

    //TODO ERROR skip doesn't respond with message but works
    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.getInteraction()
                .getGuild()
                .flatMap(trackSchedulerService::skip)
                .flatMap(trackQueueElement -> event.reply("Skipping " + trackQueueElement.getAudioTrack().getInfo().title))
                //TODO weird error handling
                .onErrorResume(throwable -> {
                    if (throwable instanceof IllegalStateException)
                        return event.reply("Nothing to skip");
                    return event.reply("Error while skipping track");
                });
    }
}
