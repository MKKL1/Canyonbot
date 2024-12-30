package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.DiscordCommand;
import com.mkkl.canyonbot.music.exceptions.*;
import com.mkkl.canyonbot.music.search.SourceRegistry;
import com.mkkl.canyonbot.music.search.sources.SearchSource;
import com.mkkl.canyonbot.music.search.handler.PlaylistResultHandler;
import com.mkkl.canyonbot.music.search.handler.SearchResultHandler;
import com.mkkl.canyonbot.music.services.PlayerService;
import com.mkkl.canyonbot.music.services.SearchService;
import com.mkkl.canyonbot.music.search.handler.TrackResultHandler;
import dev.arbjerg.lavalink.client.player.*;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.object.entity.channel.AudioChannel;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@DiscordCommand
public class PlayCommand extends BotCommand {
    private final SearchService searchService;
    private final SourceRegistry sourceRegistry;
    private final PlaylistResultHandler playlistResultHandler;
    private final TrackResultHandler trackResultHandler;
    private final SearchResultHandler searchResultHandler;
    private final PlayerService playerService;

    public PlayCommand(SearchService searchService,
                       SourceRegistry sourceRegistry,
                       DefaultErrorHandler errorHandler,
                       PlaylistResultHandler playlistResultHandler,
                       TrackResultHandler trackResultHandler, SearchResultHandler searchResultHandler, PlayerService playerService) {
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
        this.playerService = playerService;
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
                .publishOn(Schedulers.boundedElastic())//to not use event threads TODO add scheduler just for play command?
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
                .flatMap(tuple -> {//TODO tuple utils
                    LavalinkLoadResult lavalinkLoadResult = tuple.getT2();
                    //TODO get handlers from beans
                    return switch (lavalinkLoadResult) {
                        case LoadFailed loadFailed -> Mono.error(new LoadFailedException(tuple.getT1(), loadFailed.getException()));
                        case PlaylistLoaded playlistLoaded -> Mono.just(playlistResultHandler.handle(context, playlistLoaded));
                        case TrackLoaded trackLoaded -> Mono.just(trackResultHandler.handle(context, trackLoaded));
                        case SearchResult searchResult -> Mono.just(searchResultHandler.handle(context, searchResult));
                        default -> Mono.error(new NoMatchException(tuple.getT1()));
                    };
                })
                .flatMap(resultHandlerResponse ->
                        //TODO if something here fails, like for example joining channel doesn't work, track is still added to queue. Find a way to fix it
                        Mono.fromRunnable(() -> enqueueTrack(playerService, context, resultHandlerResponse.getTrack()))
                        .then(joinChannel(playerService, context))
                        .then(playerService.beginPlayback(context.guild.getId().asLong()))
                        .then(context.event.createFollowup(resultHandlerResponse.getResponse().asFollowupSpec())
                                .filter(ignore -> resultHandlerResponse.getResponse().getInteraction() != null)
                                .flatMap(message -> resultHandlerResponse.getResponse().getInteraction().interaction(message))
                        )
                );
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

    //This method is not needed
    private static void enqueueTrack(PlayerService playerService, Context context, Track track) {
        playerService.addTrackToQueue(context.guild.getId().asLong(),
                track,
                context.event.getInteraction().getUser());
    }

    private static Mono<Void> joinChannel(PlayerService playerService, Context context) {
        Interaction interaction = context.event.getInteraction();
        return context.channel
                //Check for audio channel caller is connected to
                .switchIfEmpty(Mono.justOrEmpty(interaction.getMember())
                        .switchIfEmpty(Mono.error(new MemberNotFoundException(interaction)))
                        .flatMap(PartialMember::getVoiceState)
                        .flatMap(VoiceState::getChannel)
                        //Check if audio channel was found, else throw exception
                        .switchIfEmpty(Mono.error(new ChannelNotFoundException(interaction))))
                .flatMap(channel -> {
                    if (!(channel instanceof AudioChannel))
                        return Mono.error(new InvalidAudioChannelException(interaction));
                    return Mono.just((AudioChannel) channel);
                })
                .flatMap(playerService::join);
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
