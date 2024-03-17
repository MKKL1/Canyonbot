package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.commands.exceptions.BotExternalException;
import com.mkkl.canyonbot.music.exceptions.GuildMusicBotNotCreated;
import com.mkkl.canyonbot.music.player.LinkContext;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import com.mkkl.canyonbot.music.player.TrackScheduler;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

@RegisterCommand
public class StopCommand extends BotCommand {
    private final LinkContextRegistry linkContextRegistry;
    public StopCommand(DefaultErrorHandler errorHandler, LinkContextRegistry linkContextRegistry) {
        super(ApplicationCommandRequest.builder()
                .name("stop")
                .description("Stops playing music")
                .build(), errorHandler);
        this.linkContextRegistry = linkContextRegistry;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.getInteraction()
                .getGuild()
                //TODO there is no check for player not being used at all at given guild
                // it could lead to unnecessary resource usage
                .flatMap(guild -> {
                    if (!linkContextRegistry.isCached(guild))
                        return Mono.error(new GuildMusicBotNotCreated(guild));
                    LinkContext linkContext = linkContextRegistry.getCached(guild).get();
                    if(linkContext.getTrackScheduler().getState() == TrackScheduler.State.STOPPED)
                        //TODO not sure if I need new class just for that
                        return Mono.error(new BotExternalException("Player is already stopped"));
                    return linkContext.getTrackScheduler().stop()
                            .then(event.reply("Stopped playing music and cleared the queue"));
                });
    }

}
