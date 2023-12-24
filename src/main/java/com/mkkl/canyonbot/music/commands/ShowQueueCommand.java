package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.music.messages.generators.QueueMessage;
import com.mkkl.canyonbot.music.messages.generators.QueueMessageGenerator;
import com.mkkl.canyonbot.music.player.GuildMusicBotService;
import com.mkkl.canyonbot.music.player.GuildTrackQueueService;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@RegisterCommand
public class ShowQueueCommand extends BotCommand {
    private final GuildTrackQueueService guildTrackQueueService;

    protected ShowQueueCommand(GuildTrackQueueService guildTrackQueueService,
                               DefaultErrorHandler errorHandler) {
        super(ApplicationCommandRequest.builder()
                .name("queue")
                .description("Shows the current queue")
                .build(), errorHandler);
        this.guildTrackQueueService = guildTrackQueueService;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.getInteraction()
                .getGuild()
                .flatMap(guild -> {
                    QueueMessage.Builder builder = QueueMessage.builder();

                    if (guildTrackQueueService.isPresent(guild))
                        builder.queueIterator(Objects.requireNonNull(guildTrackQueueService.iterator(guild)));

                    QueueMessage queueMessage = builder
                            .page(0)
                            .elementsPerPage(20)
                            .caller(event.getInteraction().getUser())
                            .build();
                    return event.reply(InteractionApplicationCommandCallbackSpec.builder()
                            .addAllEmbeds(queueMessage.getMessage().embeds())
                            .addAllComponents(queueMessage.getMessage().components())
                            .build());

                })
                .then();

    }
}
