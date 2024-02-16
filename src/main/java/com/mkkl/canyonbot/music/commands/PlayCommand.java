package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.music.exceptions.*;
import com.mkkl.canyonbot.music.search.SearchService;
import com.mkkl.canyonbot.music.search.SearchResult;
import com.mkkl.canyonbot.music.search.SourceRegistry;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.mkkl.canyonbot.music.services.*;
import com.mkkl.canyonbot.music.services.search.PlaylistResultHandler;
import com.mkkl.canyonbot.music.services.search.TrackResultHandler;
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

import java.util.*;

//TODO refactoring needed, this class handles too many operations
@RegisterCommand
public class PlayCommand extends BotCommand {
    private final SearchService searchService;
    private final SourceRegistry sourceRegistry;
    private final GuildMusicBotService guildMusicBotService;
    private final PlaylistResultHandler playlistResultHandler;
    private final TrackResultHandler trackResultHandler;

    public PlayCommand(SearchService searchService,
                       SourceRegistry sourceRegistry,
                       GuildMusicBotService guildMusicBotService,
                       DefaultErrorHandler errorHandler,
                       PlaylistResultHandler playlistResultHandler,
                       TrackResultHandler trackResultHandler) {
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
        this.guildMusicBotService = guildMusicBotService;
        this.playlistResultHandler = playlistResultHandler;
        this.trackResultHandler = trackResultHandler;
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
                .then(event.getInteraction().getGuild().doOnNext(guildMusicBotService::createGuildMusicBot))
                .flatMap(guild -> handleQuery(new Context(event, query, sourceId, channel, guild)))
                .then();
    }

    private Mono<?> handleQuery(Context context) {
        return Mono.justOrEmpty(context.query)
                .publishOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new QueryNotFoundException(context.event.getInteraction())))
                .zipWhen(query -> {
                    if (context.sourceId.isEmpty())
                        return searchService.search(query);

                    Optional<SearchSource> searchSource = sourceRegistry.getSource(context.sourceId.get());
                    if(searchSource.isEmpty())
                        return Mono.error(new SourceNotFoundException(context.sourceId.get()));
                    return searchService.search(query, searchSource.get());
                })
                .flatMap(tuple -> {
                    SearchResult searchResult = tuple.getT2();
                    if (searchResult.getPlaylists() != null && !searchResult.getPlaylists().isEmpty()) {
                        //SearchResult is a playlist
                        return playlistResultHandler.handle(context, searchResult);
                    } else if (searchResult.getTracks() != null && !searchResult.getTracks().isEmpty()) {
                        //SearchResult is a track
                        return trackResultHandler.handle(context, searchResult);
                    }
                    return Mono.error(new NoMatchException(tuple.getT1()));
                });
    }

    private static Collection<ImmutableApplicationCommandOptionChoiceData> sourcesAsChoices(List<SearchSource> sources) {
        List<ImmutableApplicationCommandOptionChoiceData> choices = new ArrayList<>();
        for (SearchSource source : sources) {
            choices.add(ImmutableApplicationCommandOptionChoiceData.builder()
                    .name(source.name())
                    .value(source.identifier())
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
