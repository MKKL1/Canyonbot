package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.DiscordCommand;
import com.mkkl.canyonbot.commands.exceptions.BotExternalException;
import com.mkkl.canyonbot.commands.exceptions.BotInternalException;
import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.music.messages.generators.QueueMessage;
import com.mkkl.canyonbot.music.player.LinkContext;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import com.mkkl.canyonbot.music.player.queue.TrackQueueInfo;
import com.mkkl.canyonbot.music.services.PlayerService;
import discord4j.common.util.Snowflake;
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

@DiscordCommand
public class ShowQueueCommand extends BotCommand {
    public static final int ELEMENTS_PER_PAGE = 20; //TODO this may be command option
    public static final String PAGE_OPTION_NAME = "page";
    public static final int FIRST_PAGE = 1;
    private final GatewayDiscordClient gateway;
    private final PlayerService playerService;

    protected ShowQueueCommand(DefaultErrorHandler errorHandler, GatewayDiscordClient gateway, PlayerService playerService) {
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
        this.gateway = gateway;
        this.playerService = playerService;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return Mono.defer(() -> Mono.justOrEmpty(event.getInteraction().getGuildId()))
                .switchIfEmpty(Mono.error(new BotInternalException("GuildId was undefined")))
                .map(Snowflake::asLong)
                .flatMap(guild -> Mono.just(new Parameters(guild, event.getInteraction().getCommandInteraction()
                        .flatMap(aci -> aci.getOption(PAGE_OPTION_NAME))
                        .flatMap(ApplicationCommandInteractionOption::getValue)
                        .map(ApplicationCommandInteractionOptionValue::asLong))))
                .flatMap(parameters -> {
                    //TODO handle exception caused by linkcontext being undefined for guild
                    TrackQueueInfo trackQueueInfo = playerService.getTrackQueueInfo(parameters.guild);
                    long page = FIRST_PAGE;
                    long maxPage = (long) Math.floor(((float) trackQueueInfo.getTrackQueue().size() / ELEMENTS_PER_PAGE) + 1);
                    if (parameters.page.isPresent()) {
                        page = parameters.page.get();
                        if (page < FIRST_PAGE) page = FIRST_PAGE;
                        else if (page > maxPage) page = maxPage;
                    }

                    QueueMessage.Builder builder = QueueMessage.builder()
                            .gateway(gateway)
                            .caller(event.getInteraction().getUser())
                            .currentTrack(trackQueueInfo.getCurrentTrack())
                            .elementsPerPage(ELEMENTS_PER_PAGE)
                            .maxPages(maxPage)
                            .page(page);

                    builder.queueIterator(Objects.requireNonNull(trackQueueInfo.getTrackQueue().iterator()));
                    Response response = builder.build().getMessage();

                    return event.reply(response.asCallbackSpec())
                            .then(event.getReply()
                                    .flatMap(message -> response.getResponseInteraction().get().interaction(message)));
                })

                .then();
    }

    @AllArgsConstructor
    private static class Parameters {
        private long guild;
        private Optional<Long> page;
    }
}
