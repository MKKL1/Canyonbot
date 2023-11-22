package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.AutoCompleteCommand;
import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.RegisterCommand;
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
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandOptionChoiceData;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        String query = event.getInteraction()
                .getCommandInteraction()
                .flatMap(commandInteraction -> commandInteraction.getOption("query"))
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElseThrow();//TODO throw exception

        String sourceId = event.getInteraction()
                .getCommandInteraction()
                .flatMap(commandInteraction -> commandInteraction.getOption("source"))
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElse(null);

        return event.deferReply()
                .then(handleQuery(event, query, sourceId))
                .then();
    }

    private Mono<Message> handleQuery(ChatInputInteractionEvent event, String query, String sourceId) {
        Mono<SearchResult> searchResultMono;
        if (sourceId == null) {
            searchResultMono = searchManager.search(query);
        } else {
            Optional<SearchSource> sourceOptional = sourceRegistry.getSource(sourceId);
            if (sourceOptional.isEmpty()) return event.editReply("Source not found");
            searchResultMono = searchManager.search(query, sourceOptional.get());
        }
        return searchResultMono
                .publishOn(Schedulers.boundedElastic())
                .flatMap(searchResult -> {
                    Mono<Message> message = event.editReply("No match found");
                    if (searchResult.getPlaylists() != null && !searchResult.getPlaylists()
                            .isEmpty()) {
                        //SearchResult is a playlist
                        message = handlePlaylist(event, searchResult, query);
                    } else if (searchResult.getTracks() != null && !searchResult.getTracks()
                            .isEmpty()) {
                        //SearchResult is a track
                        message = handleTrack(event, searchResult, query);
                    }

                    return message;
                })
                //TODO add identifier to thrown errors so that full tracelog could be found easier in log files
                .onErrorResume(throwable -> throwable instanceof NoMatchException,
                        throwable -> event.createFollowup(InteractionFollowupCreateSpec.builder()
                                .addEmbed(ErrorMessage.of(event.getInteraction()
                                                .getUser(), (NoMatchException) throwable)
                                        .getSpec())
                                .build()))
                .onErrorResume(throwable -> event.editReply("Error:" + throwable.getMessage())); //TODO log error, and send short message to user
    }

    private Mono<Message> handlePlaylist(ChatInputInteractionEvent event, SearchResult searchResult, String query) {
        //TODO not all tracks from playlist are loaded
        //TODO no title for playlist
        //TODO handle null on selected track
        //TODO this way of building message is too complicated and not clear, it should be refactored
        assert searchResult.getPlaylists() != null;
        ShortPlaylistMessage shortPlaylistMessage = ShortPlaylistMessage.builder()
                .setPlaylist(searchResult.getPlaylists()
                        .getFirst())
                .setSource(searchResult.getSource())
                .setUser(event.getInteraction()
                        .getUser())
                .build();

        return event.createFollowup(InteractionFollowupCreateSpec.builder()
                .addEmbed(AudioTrackMessage.builder()
                        .setAudioTrack(searchResult.getPlaylists()
                                .getFirst()
                                .getSelectedTrack())
                        .setSource(searchResult.getSource())
                        .setQuery(query)
                        .setUser(event.getInteraction()
                                .getUser())
                        .build()
                        .getSpec())
                .addEmbed(shortPlaylistMessage.getSpec())
                .addComponent(shortPlaylistMessage.getActionRow(client))
                .build());
    }

    private Mono<Message> handleTrack(ChatInputInteractionEvent event, SearchResult searchResult, String query) {
        AudioTrack track = searchResult.getTracks()
                .get(0);
        //First adds track to queue and then sends confirmation message
        return playTrack(event, track)
                .then(event.createFollowup(InteractionFollowupCreateSpec.builder()
                        .addEmbed(AudioTrackMessage.builder()
                                .setAudioTrack(track)
                                .setSource(searchResult.getSource())
                                .setQuery(query)
                                .setUser(event.getInteraction()
                                        .getUser())
                                .build()
                                .getSpec())
                        .build()));
    }

    //TODO very bad, should be refactored
    private Mono<Void> playTrack(ChatInputInteractionEvent event, AudioTrack track) {
        return Mono.fromRunnable(() -> {
            GuildMusicBotManager guildMng = musicPlayerManager.getOrCreatePlayer(event.getInteraction()
                    .getGuild()
                    .block());//TODO not sure if it can be blocking
            guildMng.getTrackQueue().enqueue(new TrackQueueElement(track, event.getInteraction().getUser()));
            if(guildMng.isConnected().block()==false)
                guildMng.join(event.getInteraction().getMember().get().getVoiceState().block().getChannel().block()).subscribe();
            if(guildMng.getTrackScheduler().getState() == TrackScheduler.State.STOPPED)
                guildMng.getTrackScheduler().start().subscribe();

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
}
