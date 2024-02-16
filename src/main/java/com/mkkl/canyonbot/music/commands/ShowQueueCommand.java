package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.music.messages.generators.QueueMessage;
import com.mkkl.canyonbot.music.services.GuildTrackQueueService;
import com.mkkl.canyonbot.music.services.GuildTrackSchedulerService;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Guild;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.AllArgsConstructor;
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
    private final Mono<GatewayDiscordClient> gateway;

    protected ShowQueueCommand(GuildTrackQueueService guildTrackQueueService,
                               DefaultErrorHandler errorHandler, GuildTrackSchedulerService guildTrackSchedulerService, Mono<GatewayDiscordClient> gateway) {
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
        this.gateway = gateway;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.getInteraction().getGuild()
                .flatMap(guild -> Mono.just(new Parameters(guild, event.getInteraction().getCommandInteraction()
                        .flatMap(aci -> aci.getOption(PAGE_OPTION_NAME))
                        .flatMap(ApplicationCommandInteractionOption::getValue)
                        .map(ApplicationCommandInteractionOptionValue::asLong))))
                .flatMap(parameters -> {
                    long page = FIRST_PAGE;
                    long maxPage = (long) Math.floor(((float) guildTrackQueueService.size(parameters.guild) / ELEMENTS_PER_PAGE) + 1);
                    if (parameters.page.isPresent()) {
                        page = parameters.page.get();
                        if (page < FIRST_PAGE) page = FIRST_PAGE;
                        else if (page > maxPage) page = maxPage;
                    }

                    QueueMessage.Builder builder = QueueMessage.builder()
                            .gateway(gateway)
                            .caller(event.getInteraction().getUser())
                            .currentTrack(guildTrackSchedulerService.getCurrentTrack(parameters.guild))
                            .elementsPerPage(ELEMENTS_PER_PAGE)
                            .maxPages(maxPage)
                            .page(page);

                    if (guildTrackQueueService.isPresent(parameters.guild))
                        builder.queueIterator(Objects.requireNonNull(guildTrackQueueService.iterator(parameters.guild)));
                    Response response = builder.build().getMessage();

                    return event.reply(response.asCallbackSpec())
                            .then(event.getReply()
                                    .flatMap(message -> response.getResponseInteraction().get().interaction(message)));
                })

                .then();
    }

//    private Tuple2<ResponseMessageData, Long> createResponse(ChatInputInteractionEvent event, Parameters parameters) {
//        QueueMessage.Builder builder = QueueMessage.builder();
//
//        if (guildTrackQueueService.isPresent(parameters.guild))
//            builder.queueIterator(Objects.requireNonNull(guildTrackQueueService.iterator(parameters.guild)));
//
//        long page = FIRST_PAGE;
//        long maxPage = (long) Math.floor(((float) guildTrackQueueService.size(parameters.guild) / ELEMENTS_PER_PAGE) + 1);
//        if (parameters.page.isPresent()) {
//            page = parameters.page.get();
//            if (page < FIRST_PAGE) page = FIRST_PAGE;
//            else if (page > maxPage) page = maxPage;
//        }
//
//        return Tuples.of(builder
//                .page(page)
//                .maxPages(maxPage)
//                .elementsPerPage(ELEMENTS_PER_PAGE)
//                .caller(event.getInteraction()
//                        .getUser())
//                .currentTrack(guildTrackSchedulerService.getCurrentTrack(parameters.guild))
//                .nextPageButton(nextPageButton)
//                .build().getMessage(), page);
//    }

    @AllArgsConstructor
    private static class Parameters {
        private Guild guild;
        private Optional<Long> page;
    }
}
