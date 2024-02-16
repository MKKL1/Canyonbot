package com.mkkl.canyonbot.music.services.search;

import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.utils.TimeoutUtils;
import com.mkkl.canyonbot.music.commands.PlayCommand;
import com.mkkl.canyonbot.music.messages.generators.ResponseMessageData;
import com.mkkl.canyonbot.music.messages.generators.ShortPlaylistMessage;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.search.SearchResult;
import com.mkkl.canyonbot.music.services.GuildTrackQueueService;
import com.mkkl.canyonbot.music.services.PlayTrackService;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.possible.Possible;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicStampedReference;

@Service
public class PlaylistResultHandler implements SearchResultHandler {
    @Autowired
    private GuildTrackQueueService guildTrackQueueService;
    @Autowired
    private PlayTrackService playTrackService;
    @Autowired
    private Mono<GatewayDiscordClient> gateway;
    @Override
    public Mono<?> handle(PlayCommand.Context context, SearchResult searchResult) {
        //TODO not all tracks from playlist are loaded
        //TODO no title for playlist
        //TODO handle null on selected track
        assert searchResult.getPlaylists() != null;
        AudioPlaylist audioPlaylist = searchResult.getPlaylists().getFirst();
        assert audioPlaylist != null;

        Response shortPlaylistMessage = ShortPlaylistMessage.builder()
                .query(context.getQuery())
                .playlist(audioPlaylist)
                .source(searchResult.getSource())
                .user(context.getEvent().getInteraction().getUser())
                .gateway(gateway)
                .onPlay(event ->
                        event.reply("Playing " + audioPlaylist.getTracks().size() + " tracks")
                                .and(Mono.fromRunnable(() ->
                                        guildTrackQueueService.addAll(
                                                context.getGuild(),
                                                TrackQueueElement.listOf(
                                                        audioPlaylist.getTracks(),
                                                        context.getEvent().getInteraction().getUser()
                                                )
                                        )))
                                .and(Mono.justOrEmpty(event.getMessage()).flatMap(TimeoutUtils::clearActionBar))
                )
                .build()
                .getMessage();

        Mono<Void> playMono = Mono.empty();
        if(audioPlaylist.getSelectedTrack() != null)
            playMono = playTrackService.playTrack(
                    context.getGuild(),
                    context.getChannel(),
                    context.getEvent().getInteraction(),
                    audioPlaylist.getSelectedTrack());

        return playMono.then(context.getEvent().createFollowup(shortPlaylistMessage.asFollowupSpec())
                        .flatMap(message -> shortPlaylistMessage.getResponseInteraction().get().interaction(message)));
    }
}
