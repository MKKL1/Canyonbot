package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.AutoCompleteCommand;
import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.commands.exceptions.ReplyMessageException;
import com.mkkl.canyonbot.music.MusicPlayerManager;
import com.mkkl.canyonbot.music.messages.AudioTrackMessage;
import com.mkkl.canyonbot.music.messages.ErrorMessage;
import com.mkkl.canyonbot.music.messages.ShortPlaylistMessage;
import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.player.queue.TrackScheduler;
import com.mkkl.canyonbot.music.search.SearchManager;
import com.mkkl.canyonbot.music.search.SearchResult;
import com.mkkl.canyonbot.music.search.SourceRegistry;
import com.mkkl.canyonbot.music.exceptions.NoMatchException;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.AudioChannel;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionChoiceData;
import discord4j.voice.VoiceConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@RegisterCommand
public class PlayCommand extends BotCommand implements AutoCompleteCommand {
    private final SearchManager searchManager;
    private final SourceRegistry sourceRegistry;
    private final DiscordClient client;
    private final MusicPlayerManager musicPlayerManager;
    private final Scheduler scheduler = Schedulers.boundedElastic();

    //private CommandOptionCompletionManager completionManager;
    public PlayCommand(SearchManager searchManager, SourceRegistry sourceRegistry, DiscordClient client, MusicPlayerManager musicPlayerManager) {
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
                .build());
        this.sourceRegistry = sourceRegistry;
        this.searchManager = searchManager;
        //completionManager = new CommandOptionCompletionManager();
        //TODO autocompletion for query. This should work by searching the index of query terms and returning the most popular ones
        //TODO autocompletion for source IS NOT NEEDED. It is already handled by discord using choices
        //completionManager.addOption("source", new CommandOptionCompletion(sourceRegistry.sourceSuggestionOptions()));
        this.client = client;
        this.musicPlayerManager = musicPlayerManager;
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

        Optional<Mono<Channel>> channel = event.getInteraction()
                .getCommandInteraction()
                .flatMap(commandInteraction -> commandInteraction.getOption("channel"))
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asChannel);

        return event.deferReply()
                .then(handleQuery(new CommandContext(event, query, sourceId, channel)))
                .then();
    }

    private Mono<Message> handleQuery(CommandContext context) {
        //TODO order of error checking is not easily modifiable, fix this
        Mono<SearchResult> searchResultMono;
        String query = context.query.orElseThrow(() -> new ReplyMessageException("Query not found"));
        if (context.sourceId.isEmpty())
            searchResultMono = searchManager.search(query);
        else
            searchResultMono = searchManager.search(query, sourceRegistry.getSource(context.sourceId.get())
                    .orElseThrow(() -> new ReplyMessageException("Source not found")));

        return searchResultMono
                .publishOn(Schedulers.boundedElastic())
                .flatMap(searchResult -> {
                    Mono<Message> message = context.event.editReply("No match found");//TODO this should be handled by NoMatchException
                    if (searchResult.getPlaylists() != null && !searchResult.getPlaylists()
                            .isEmpty()) {
                        //SearchResult is a playlist
                        message = handlePlaylist(context, searchResult);
                    } else if (searchResult.getTracks() != null && !searchResult.getTracks()
                            .isEmpty()) {
                        //SearchResult is a track
                        message = handleTrack(context, searchResult);
                    }

                    return message;
                })
                //TODO add identifier to thrown errors so that full tracelog could be found easier in log files
                .onErrorResume(throwable -> throwable instanceof ReplyMessageException,
                        throwable -> context.event.createFollowup(InteractionFollowupCreateSpec.builder()
                                .addEmbed(ErrorMessage.of(context.event.getInteraction()
                                                .getUser(), (ReplyMessageException) throwable)
                                        .getSpec())
                                .build()))
                .onErrorResume(throwable -> context.event.editReply("Error:" + throwable.getMessage())); //TODO log error, and send short message to user
    }

    private Mono<Message> handlePlaylist(CommandContext context, SearchResult searchResult) {
        //TODO not all tracks from playlist are loaded
        //TODO no title for playlist
        //TODO handle null on selected track
        //TODO this way of building message is too complicated and not clear, it should be refactored
        assert searchResult.getPlaylists() != null;
        ShortPlaylistMessage shortPlaylistMessage = ShortPlaylistMessage.builder()
                .setPlaylist(searchResult.getPlaylists().getFirst())
                .setSource(searchResult.getSource())
                .setUser(context.event.getInteraction().getUser())
                .build();

        return context.event.createFollowup(InteractionFollowupCreateSpec.builder()
                .addEmbed(AudioTrackMessage.builder()
                        .setAudioTrack(searchResult.getPlaylists()
                                .getFirst()
                                .getSelectedTrack())
                        .setSource(searchResult.getSource())
                        .setQuery(context.query.orElseThrow(() -> new IllegalStateException("Query not found"))) //Should never happen
                        .setUser(context.event.getInteraction()
                                .getUser())
                        .build()
                        .getSpec())
                .addEmbed(shortPlaylistMessage.getSpec())
                .addComponent(shortPlaylistMessage.getActionRow(client))
                .build());
    }

    private Mono<Message> handleTrack(CommandContext context, SearchResult searchResult) {
        AudioTrack track = searchResult.getTracks()
                .get(0);
        //First adds track to queue and then sends confirmation message
        return playTrack(context, track)
                .then(context.event.createFollowup(InteractionFollowupCreateSpec.builder()
                        .addEmbed(AudioTrackMessage.builder()
                                .setAudioTrack(track)
                                .setSource(searchResult.getSource())
                                .setQuery(context.query.orElseThrow(() -> new IllegalStateException("Query not found")))
                                .setUser(context.event.getInteraction()
                                        .getUser())
                                .build()
                                .getSpec())
                        .build()));
    }

    //TODO this part of code requires cleanup, there are too many nested monos
    private Mono<Void> playTrack(CommandContext context, AudioTrack track) {
        return context.event.getInteraction()
                .getGuild()
                .flatMap(guild -> Mono.just(musicPlayerManager.getOrCreatePlayer(guild)))
                .flatMap(guildMng -> {
                    Mono<Void> enqueueMono = Mono.fromRunnable(() -> guildMng.getTrackQueue()
                            .enqueue(new TrackQueueElement(track, context.event.getInteraction()
                                    .getUser())));
                    Mono<VoiceConnection> joinMono = guildMng.isConnected()
                            .flatMap(isConnected -> {
                                if (isConnected) return Mono.empty();
                                //TODO if user is not in channel error won't be caught
                                return context.channel.orElse(context.event.getInteraction()
                                                .getMember()
                                                .orElseThrow(() -> new ReplyMessageException("Member not found"))
                                                .getVoiceState()
                                                .flatMap(VoiceState::getChannel))
                                        .flatMap(channel -> {
                                            if (!(channel instanceof AudioChannel))
                                                return Mono.error(new ReplyMessageException(channel.getMention() + " is not Audio Channel"));
                                            return Mono.just((AudioChannel) channel);
                                        })
                                        .flatMap(guildMng::join);
                            });
                    Mono<Void> startMono = Mono.just(guildMng.getTrackScheduler())
                            .filter(scheduler -> scheduler.getState() == TrackScheduler.State.STOPPED)
                            .flatMap(TrackScheduler::start);
                    return enqueueMono.then(joinMono).then(startMono);
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
        private final Optional<Mono<Channel>> channel;
    }
}
