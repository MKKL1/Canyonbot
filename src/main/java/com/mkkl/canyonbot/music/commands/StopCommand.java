package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.commands.exceptions.UserResponseMessage;
import com.mkkl.canyonbot.music.exceptions.GuildMusicBotNotCreated;
import com.mkkl.canyonbot.music.player.TrackScheduler;
import com.mkkl.canyonbot.music.services.GuildTrackSchedulerService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

@RegisterCommand
public class StopCommand extends BotCommand {
    private final GuildTrackSchedulerService trackSchedulerService;

    public StopCommand(GuildTrackSchedulerService trackSchedulerService, DefaultErrorHandler errorHandler) {
        super(ApplicationCommandRequest.builder()
                .name("stop")
                .description("Stops playing music")
                .build(), errorHandler);
        this.trackSchedulerService = trackSchedulerService;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.getInteraction()
                .getGuild()
                //TODO there is no check for player not being used at all at given guild
                // it could lead to unnecessary resource usage
                .flatMap(guild -> {
                    if (!trackSchedulerService.isPresent(guild))
                        return Mono.error(new GuildMusicBotNotCreated(guild));
                    if(trackSchedulerService.getState(guild) == TrackScheduler.State.STOPPED)
                        //TODO not sure if I need new class just for that
                        return Mono.error(new UserResponseMessage("Player is already stopped"));
                    return Mono.fromRunnable(() -> trackSchedulerService.stopPlaying(guild))
                            .then(event.reply("Stopped playing music and cleared the queue"));
                });
    }

}
