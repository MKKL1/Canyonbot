package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.commands.exceptions.UserResponseMessage;
import com.mkkl.canyonbot.music.messages.generators.QueueMessage;
import com.mkkl.canyonbot.music.services.GuildTrackQueueService;
import com.mkkl.canyonbot.music.services.GuildTrackSchedulerService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Guild;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@RegisterCommand
public class ShowQueueCommand extends BotCommand {
    public static final int ELEMENTS_PER_PAGE = 20; //TODO this may be command option
    public static final String PAGE_OPTION_NAME = "page";
    public static final int FIRST_PAGE = 1;
    private final GuildTrackQueueService guildTrackQueueService;
    private final GuildTrackSchedulerService guildTrackSchedulerService;

    protected ShowQueueCommand(GuildTrackQueueService guildTrackQueueService,
                               DefaultErrorHandler errorHandler, GuildTrackSchedulerService guildTrackSchedulerService) {
        super(ApplicationCommandRequest.builder()
                .name("queue")
                .description("Shows the current queue")
                .addOption(ApplicationCommandOptionData.builder()
                        .name(PAGE_OPTION_NAME)
                        .type(ApplicationCommandOption.Type.INTEGER.getValue())
                        .description("page")
                        .required(false)
                        .build())
                .build(), errorHandler);
        this.guildTrackQueueService = guildTrackQueueService;
        this.guildTrackSchedulerService = guildTrackSchedulerService;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.getInteraction().getGuild()
                .flatMap(guild -> Mono.just(new Parameters(guild, event.getInteraction().getCommandInteraction()
                        .flatMap(aci -> aci.getOption(PAGE_OPTION_NAME))
                        .flatMap(ApplicationCommandInteractionOption::getValue)
                        .map(ApplicationCommandInteractionOptionValue::asLong))))

                .flatMap(parameters -> {
                    QueueMessage.Builder builder = QueueMessage.builder();

                    if (guildTrackQueueService.isPresent(parameters.guild))
                        builder.queueIterator(Objects.requireNonNull(guildTrackQueueService.iterator(parameters.guild)));

                    long page = FIRST_PAGE;
                    long maxPage = (long) Math.floor(((float) guildTrackQueueService.size(parameters.guild) / ELEMENTS_PER_PAGE) + 1);
                    if (parameters.page.isPresent()) {
                        page = parameters.page.get();
                        if (page < FIRST_PAGE) page = FIRST_PAGE;
                        else if (page > maxPage) page = maxPage;
                    }

                    return Mono.just(builder
                            .page(page)
                            .maxPages(maxPage)
                            .elementsPerPage(ELEMENTS_PER_PAGE)
                            .caller(event.getInteraction()
                                    .getUser())
                            .currentTrack(guildTrackSchedulerService.getCurrentTrack(parameters.guild))
                            .build());
                })
                .flatMap(queueMessage -> Mono.just(queueMessage.getMessage()))
                .flatMap(messageData -> event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .addAllEmbeds(messageData.embeds())
                        .addAllComponents(messageData.components())
                        .build()))
                .then();

    }

    @AllArgsConstructor
    private static class Parameters {
        private Guild guild;
        private Optional<Long> page;
    }
}
