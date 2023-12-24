package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.music.messages.generators.QueueMessage;
import com.mkkl.canyonbot.music.messages.generators.QueueMessageGenerator;
import com.mkkl.canyonbot.music.player.GuildMusicBotService;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

@RegisterCommand
public class ShowQueueCommand extends BotCommand {
    private final GuildMusicBotService guildMusicBotService;

    protected ShowQueueCommand(GuildMusicBotService guildMusicBotService,
                               DefaultErrorHandler errorHandler) {
        super(ApplicationCommandRequest.builder()
                .name("queue")
                .description("Shows the current queue")
                .build(), errorHandler);
        this.guildMusicBotService = guildMusicBotService;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.getInteraction()
                .getGuild()
                .flatMap(guild -> Mono.justOrEmpty(guildMusicBotService.getGuildMusicBot(guild)))
                .flatMap(guildMusicBot -> {
                    QueueMessage<TrackQueueElement> queueMessage = QueueMessage.builder()
                            .queue(guildMusicBot.getTrackQueue())
                            .page(0)
                            .elementsPerPage(20)
                            .caller(event.getInteraction().getUser())
                            .build();
                    return event.createFollowup(InteractionFollowupCreateSpec.builder()
                        .addAllEmbeds(queueMessage.getMessage().embeds())
                        .addAllComponents(queueMessage.getMessage().components())
                        .build());

                })
                .then();

    }
}
