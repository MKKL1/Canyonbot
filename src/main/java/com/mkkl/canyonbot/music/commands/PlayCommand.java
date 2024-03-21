package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.music.exceptions.*;
import com.mkkl.canyonbot.music.services.search.SearchResultHandler;
import com.mkkl.canyonbot.music.services.search.SearchService;
import com.mkkl.canyonbot.music.search.SourceRegistry;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.mkkl.canyonbot.music.services.search.PlaylistResultHandler;
import com.mkkl.canyonbot.music.services.search.TrackResultHandler;
import dev.arbjerg.lavalink.client.protocol.*;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionChoiceData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.InvalidParameterException;
import java.util.*;

//TODO refactoring needed, this class handles too many operations
@RegisterCommand
public class PlayCommand extends BotCommand {
    private final SearchService searchService;
    private final SourceRegistry sourceRegistry;
    private final PlaylistResultHandler playlistResultHandler;
    private final TrackResultHandler trackResultHandler;
    private final SearchResultHandler searchResultHandler;

    public PlayCommand(SearchService searchService,
                       SourceRegistry sourceRegistry,
                       DefaultErrorHandler errorHandler,
                       PlaylistResultHandler playlistResultHandler,
                       TrackResultHandler trackResultHandler, SearchResultHandler searchResultHandler) {
        super(ApplicationCommandRequest.builder()
                .name("play")
                .description("Play a song")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("query")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .description("url or search query")
                        .required(true)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name("source")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .description("source to search from")
                        .choices(sourcesAsChoices(sourceRegistry.getSourceList()))
                        .required(false)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name("seek")
                        .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                        .description("seeking to provided timestamp")
                        .choices(ApplicationCommandOptionChoiceData.builder()
                                        .name("Enable seeking")
                                        .value("true")
                                        .build(),
                                ApplicationCommandOptionChoiceData.builder()
                                        .name("Disable seeking")
                                        .value("false")
                                        .build())
                        .required(false)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name("channel")
                        .type(ApplicationCommandOption.Type.CHANNEL.getValue())
                        .description("Channel to play in")
                        .required(false)
                        .build())
                .build(), errorHandler);
        this.sourceRegistry = sourceRegistry;
        this.searchService = searchService;
        this.playlistResultHandler = playlistResultHandler;
        this.trackResultHandler = trackResultHandler;
        this.searchResultHandler = searchResultHandler;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        //TODO find a way to make this more generic
        Optional<String> query = event.getInteraction()
                .getCommandInteraction()
                .flatMap(commandInteraction -> commandInteraction.getOption("query"))
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString);

        Optional<String> sourceId = event.getInteraction()
                .getCommandInteraction()
                .flatMap(commandInteraction -> commandInteraction.getOption("source"))
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString);

        Mono<Channel> channel = event.getInteraction()
                .getCommandInteraction()
                .flatMap(commandInteraction -> commandInteraction.getOption("channel"))
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asChannel)
                .orElse(Mono.empty());



        return event.deferReply()
                .then(event.getInteraction().getGuild())
                .flatMap(guild -> handleQuery(new Context(event, query, sourceId, channel, guild)))
                .then();
    }

    private Mono<?> handleQuery(Context context) {
        return Mono.justOrEmpty(context.query)
                .publishOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new QueryNotFoundException(context.event.getInteraction())))
                .zipWhen(query -> {
                    if (context.sourceId.isEmpty())
                        return searchService.search(context.guild, query);

                    SearchSource searchSource;
                    try {
                        searchSource = sourceRegistry.getSource(context.sourceId.get());
                    } catch (InvalidParameterException e) {
                        //TODO getSource needs it's own exception, or a better way to handle this
                        return Mono.error(new SourceNotFoundException(context.sourceId.get()));
                    }

                    return searchService.search(context.guild, query, searchSource);
                })
                .flatMap(tuple -> {
                    LavalinkLoadResult lavalinkLoadResult = tuple.getT2();
                    return switch (lavalinkLoadResult) {
                        case LoadFailed loadFailed -> Mono.error(new LoadFailedException(tuple.getT1(), loadFailed.getException()));
                        case PlaylistLoaded playlistLoaded -> playlistResultHandler.handle(context, playlistLoaded);
                        case TrackLoaded trackLoaded -> trackResultHandler.handle(context, trackLoaded);
                        case SearchResult searchResult -> searchResultHandler.handle(context, searchResult);
                        default -> Mono.error(new NoMatchException(tuple.getT1()));
                    };
                });
    }

    private static Collection<ImmutableApplicationCommandOptionChoiceData> sourcesAsChoices(List<SearchSource> sources) {
        List<ImmutableApplicationCommandOptionChoiceData> choices = new ArrayList<>();
        for (SearchSource source : sources) {
            choices.add(ImmutableApplicationCommandOptionChoiceData.builder()
                    .name(source.name())
                    .value(source.prefix())
                    .build());
        }
        return choices;
    }


    @AllArgsConstructor
    @Getter
    public static class Context {
        private final ChatInputInteractionEvent event;
        private final Optional<String> query;
        private final Optional<String> sourceId;
        private final Mono<Channel> channel;
        private final Guild guild;
    }
}
