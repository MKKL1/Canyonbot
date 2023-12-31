package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.music.buttons.NextPageButton;
import com.mkkl.canyonbot.music.services.GuildTrackQueueService;
import com.mkkl.canyonbot.music.services.GuildTrackSchedulerService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Guild;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RegisterCommand
public class ShowQueueCommand extends BotCommand {
    public static final int ELEMENTS_PER_PAGE = 20; //TODO this may be command option
    public static final String PAGE_OPTION_NAME = "page";
    public static final int FIRST_PAGE = 1;
    private final GuildTrackQueueService guildTrackQueueService;
    private final GuildTrackSchedulerService guildTrackSchedulerService;
    private final NextPageButton nextPageButton;

    protected ShowQueueCommand(GuildTrackQueueService guildTrackQueueService,
                               DefaultErrorHandler errorHandler, GuildTrackSchedulerService guildTrackSchedulerService, NextPageButton nextPageButton) {
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
        this.nextPageButton = nextPageButton;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
//        return event.getInteraction().getGuild()
//                .flatMap(guild -> Mono.just(new Parameters(guild, event.getInteraction().getCommandInteraction()
//                        .flatMap(aci -> aci.getOption(PAGE_OPTION_NAME))
//                        .flatMap(ApplicationCommandInteractionOption::getValue)
//                        .map(ApplicationCommandInteractionOptionValue::asLong))))
//                .flatMap(parameters -> {
//                    //Quick and dirty solution
//                    Tuple2<ResponseMessageData, Long> messageDataTuple = createResponse(event, parameters);
//                    ResponseMessageData messageData = messageDataTuple.getT1();
//                    AtomicLong page = new AtomicLong(messageDataTuple.getT2());
//
//                    return event.reply(InteractionApplicationCommandCallbackSpec.builder()
//                                    .embeds()
//                            .addAllEmbeds(messageData.embeds())
//                            .addAllComponents(messageData.components())
//                            .build())
//                            .then(nextPageButton.onInteraction()
//                                    .filterWhen(buttonInteractionEvent -> event.getReply().flatMap(message -> Mono.just(message.getId().equals(buttonInteractionEvent.getMessageId()))))
//                                    .flatMap(buttonInteractionEvent -> {
//                                        page.addAndGet(1);
//                                        Tuple2<ResponseMessageData, Long> newResponseDataTuple = createResponse(event, new Parameters(parameters.guild, Optional.of(page.get())));
//                                        ResponseMessageData newResponseData = newResponseDataTuple.getT1();
//
//                                        return buttonInteractionEvent.deferReply().then(buttonInteractionEvent.getInteractionResponse().deleteInitialResponse()).then(
//                                                event.editReply(InteractionReplyEditSpec.builder()
//                                                .addAllEmbeds(newResponseData.embeds())
//                                                .addAllComponents(newResponseData.components())
//                                                .build()));
//
//                                    })
//                                    .timeout(Duration.ofSeconds(60))
//                                    .onErrorResume(TimeoutException.class, ignore ->
//                                            event.editReply().withComponents(Possible.of(Optional.of(Collections.emptyList()))))
//                                    .then()
//                            );
//                })
//                        //.filter(buttonInteractionEvent -> buttonInteractionEvent.getMessageId() == event.getReply()))
//
//                .then();
        return Mono.empty();

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
