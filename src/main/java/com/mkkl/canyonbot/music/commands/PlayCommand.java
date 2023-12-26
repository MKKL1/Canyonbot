package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.AutoCompleteCommand;
import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.commands.exceptions.ReplyMessageException;
import com.mkkl.canyonbot.discord.GuildVoiceConnectionService;
import com.mkkl.canyonbot.music.buttons.PlaylistAddAllButton;
import com.mkkl.canyonbot.music.exceptions.*;
import com.mkkl.canyonbot.music.messages.generators.*;
import com.mkkl.canyonbot.music.player.*;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.search.SearchService;
import com.mkkl.canyonbot.music.search.SearchResult;
import com.mkkl.canyonbot.music.search.SourceRegistry;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.object.entity.channel.AudioChannel;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionChoiceData;
import discord4j.discordjson.possible.Possible;
import discord4j.voice.VoiceConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;

//TODO refactoring needed, this class handles too many operations
@RegisterCommand
public class PlayCommand extends BotCommand implements AutoCompleteCommand {
    private final SearchService searchService;
    private final SourceRegistry sourceRegistry;
    private final GuildTrackSchedulerService trackSchedulerService;
    private final GuildVoiceConnectionService voiceConnectionService;
    private final GuildMusicBotService guildMusicBotService;
    private final GuildPlaylistMessageService guildPlaylistMessageService;
    private final GuildTrackQueueService guildTrackQueueService;
    private final DiscordClient client;

    @Autowired
    private PlaylistAddAllButton playlistAddAllButton;

    //private CommandOptionCompletionManager completionManager;
    public PlayCommand(SearchService searchService,
                       SourceRegistry sourceRegistry,
                       GuildTrackSchedulerService trackSchedulerService,
                       GuildMusicBotService guildMusicBotService,
                       GuildVoiceConnectionService voiceConnectionService,
                       DefaultErrorHandler errorHandler,
                       GuildPlaylistMessageService guildPlaylistMessageService, GuildTrackQueueService guildTrackQueueService, DiscordClient client) {
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
        //completionManager = new CommandOptionCompletionManager();
        //TODO autocompletion for query. This should work by searching the index of query terms and returning the most popular ones
        //TODO autocompletion for source IS NOT NEEDED. It is already handled by discord using choices
        //completionManager.addOption("source", new CommandOptionCompletion(sourceRegistry.sourceSuggestionOptions()));
        this.trackSchedulerService = trackSchedulerService;
        this.guildMusicBotService = guildMusicBotService;
        this.voiceConnectionService = voiceConnectionService;
        this.guildPlaylistMessageService = guildPlaylistMessageService;
        this.guildTrackQueueService = guildTrackQueueService;
        this.client = client;
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
                .then(handleQuery(new CommandContext(event, query, sourceId, channel)))
                .then();
    }

    private Mono<Message> handleQuery(CommandContext context) {
        //TODO order of error checking is not easily modifiable, fix this
        Mono<SearchResult> searchResultMono;
        if(context.query.isEmpty())
            return Mono.error(new QueryNotFoundException(context.event.getInteraction()));
        String query = context.query.get();
        if (context.sourceId.isEmpty())
            searchResultMono = searchService.search(query);
        else {
            Optional<SearchSource> searchSource = sourceRegistry.getSource(context.sourceId.get());
            if(searchSource.isEmpty())
                return Mono.error(new SourceNotFoundException(context.sourceId.get()));
            searchResultMono = searchService.search(query, searchSource.get());
        }

        return searchResultMono
                .publishOn(Schedulers.boundedElastic())
                .flatMap(searchResult -> {
                    Mono<Message> message = Mono.empty();
                    if (searchResult.getPlaylists() != null && !searchResult.getPlaylists().isEmpty()) {
                        //SearchResult is a playlist
                        message = handlePlaylist(context, searchResult);
                    } else if (searchResult.getTracks() != null && !searchResult.getTracks().isEmpty()) {
                        //SearchResult is a track
                        message = handleTrack(context, searchResult);
                    } else message = Mono.error(new NoMatchException(query));
                    return message;
                });
    }

    private Mono<Message> handlePlaylist(CommandContext context, SearchResult searchResult) {
        //TODO not all tracks from playlist are loaded
        //TODO no title for playlist
        //TODO handle null on selected track
        assert searchResult.getPlaylists() != null;
        AudioPlaylist audioPlaylist = searchResult.getPlaylists().getFirst();
        assert audioPlaylist != null;
        ResponseMessageData shortPlaylistMessage = ShortPlaylistMessage.builder()
                .query(context.query)
                .playlist(audioPlaylist)
                .source(searchResult.getSource())
                .user(context.event.getInteraction().getUser())
                .build()
                .getMessage();

        Mono<Void> playMono = Mono.empty();
        if(audioPlaylist.getSelectedTrack() != null)
            playMono = playTrack(context, audioPlaylist.getSelectedTrack());

//        playlistAddAllButton.onClick()
//                .filter(event -> event.getMessageId().equals(message.getId()))
//                ...
//                .


        return playMono
                .then(context.event.createFollowup(InteractionFollowupCreateSpec.builder()
                        .addAllEmbeds(shortPlaylistMessage.embeds())
                        .addAllComponents(shortPlaylistMessage.components())
                        .build())
                        .flatMap(message -> playlistAddAllButton.onInteraction()
                                .filter(event -> event.getMessageId().equals(message.getId()))
                                .flatMap(event -> event.reply("Playing " + audioPlaylist.getTracks().size() + " tracks"))
                                .flatMap(event -> Mono.just(message))
                                .timeout(Duration.ofSeconds(5))
                                .onErrorResume(TimeoutException.class, ignore -> {
                                    System.out.println("siema");
                                    return message.edit().withComponents(ActionRow.of(Button.link("https://github.com/Discord4J/Discord4J/blob/master/core/src/test/java/discord4j/core/ExampleInteractions.java#L218", "URL")));
                                })
                                .then(Mono.just(message))
                        )
                );
    }

    private Mono<Message> handleTrack(CommandContext context, SearchResult searchResult) {
        assert searchResult.getTracks() != null;
        AudioTrack track = searchResult.getTracks().getFirst();
        //First adds track to queue and then sends confirmation message
        return playTrack(context, track)
                .then(context.event.createFollowup(InteractionFollowupCreateSpec.builder()
                        .addAllEmbeds(AudioTrackMessage.builder()
                                .audioTrack(track)
                                .source(searchResult.getSource())
                                .query(context.query.orElseThrow(() -> new IllegalStateException("Query not found")))
                                .user(context.event.getInteraction().getUser())
                                .build()
                                .getMessage().embeds())
                        .build()));
    }

    private Mono<Void> playTrack(CommandContext context, AudioTrack track) {
        return context.event.getInteraction()
            .getGuild()
            .zipWhen(guild -> Mono.just(guildMusicBotService.getGuildMusicBot(guild)
                    .orElse(guildMusicBotService.createGuildMusicBot(guild))), Tuples::of)
            .flatMap(tuple -> {
                Guild guild = tuple.getT1();
                GuildMusicBot guildMusicBot = tuple.getT2();

                Mono<Void> enqueueMono = Mono.fromRunnable(() -> guildMusicBot.getTrackQueue()
                        .add(new TrackQueueElement(track, context.event.getInteraction().getUser())));

                Mono<Void> joinMono = voiceConnectionService.isConnected(guild)
                    .filter(isConnected -> !isConnected)
                    .flatMap(ignore -> context.channel
                            //Check for audio channel caller is connected to
                            .switchIfEmpty(Mono.justOrEmpty(context.event.getInteraction().getMember())
                                .switchIfEmpty(Mono.error(new MemberNotFoundException(context.event.getInteraction())))
                                .flatMap(PartialMember::getVoiceState)
                                .flatMap(VoiceState::getChannel)
                                //Check if audio channel was found, else throw exception
                                .switchIfEmpty(Mono.error(new ChannelNotFoundException(context.event.getInteraction()))))
                            .flatMap(channel -> {
                                if (!(channel instanceof AudioChannel))
                                    return Mono.error(new InvalidAudioChannelException(context.event.getInteraction()));
                                return Mono.just((AudioChannel) channel);
                            })
                            .flatMap(audioChannel -> voiceConnectionService.join(guild, guildMusicBot.getPlayer().getAudioProvider(), audioChannel)))
                    .then();


                Mono<Void> startMono = trackSchedulerService.getState(guild) == TrackScheduler.State.STOPPED
                        //TODO I am not sure if startPlaying should be mono or not
                    ? Mono.fromRunnable(() -> trackSchedulerService.startPlaying(guild))
                    : Mono.empty();
                return enqueueMono
                        .and(joinMono)
                        .then(startMono);
            });
    }

    @Override
    public Mono<Void> autoComplete(ChatInputAutoCompleteEvent event) {
        //TODO use scheduler
        //return completionManager.handleAutoCompletionEvent(event);//TODO redis may be used to perform searches
        return Mono.empty();
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
    private static class CommandContext {
        private final ChatInputInteractionEvent event;
        private final Optional<String> query;
        private final Optional<String> sourceId;
        private final Mono<Channel> channel;
    }
}
