package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.DiscordCommand;
import com.mkkl.canyonbot.commands.exceptions.BotInternalException;
import com.mkkl.canyonbot.music.services.PlayerService;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

@DiscordCommand
public class StopCommand extends BotCommand {
    private final PlayerService playerService;
    public StopCommand(DefaultErrorHandler errorHandler, PlayerService playerService) {
        super(ApplicationCommandRequest.builder()
                .name("stop")
                .description("Stops playing music")
                .build(), errorHandler);
        this.playerService = playerService;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return Mono.defer(() -> Mono.justOrEmpty(event.getInteraction().getGuildId()))
                .switchIfEmpty(Mono.error(new BotInternalException("GuildId was undefined")))
                .doOnNext(guildId -> playerService.clearQueue(guildId.asLong()))
                .flatMap(guildId -> playerService.leaveChannel(guildId).thenReturn(guildId))
                .flatMap(guildId -> playerService.destroyLinkContext(guildId.asLong()))
                .then(Mono.defer(() -> event.reply("Disconnecting")));
    }

}
