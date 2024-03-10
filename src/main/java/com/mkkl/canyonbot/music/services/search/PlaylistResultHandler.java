package com.mkkl.canyonbot.music.services.search;

import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.utils.TimeoutUtils;
import com.mkkl.canyonbot.music.commands.PlayCommand;
import com.mkkl.canyonbot.music.messages.generators.ShortPlaylistMessage;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.services.GuildTrackQueueService;
import com.mkkl.canyonbot.music.services.PlayTrackService;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import dev.arbjerg.lavalink.client.protocol.PlaylistLoaded;
import discord4j.core.GatewayDiscordClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PlaylistResultHandler implements SearchResultHandler<PlaylistLoaded> {
    @Autowired
    private GuildTrackQueueService guildTrackQueueService;
    @Autowired
    private PlayTrackService playTrackService;
    @Autowired
    private Mono<GatewayDiscordClient> gateway;

    @Override
    public Mono<?> handle(PlayCommand.Context context, PlaylistLoaded playlistLoaded) {
        Response shortPlaylistMessage = ShortPlaylistMessage.builder()
                .query(context.getQuery())
                .playlist(playlistLoaded)
                .user(context.getEvent().getInteraction().getUser())
                .gateway(gateway)
                .onPlay(event ->
                        event.reply("Playing " + playlistLoaded.getTracks().size() + " tracks")
                                .and(Mono.fromRunnable(() ->
                                        guildTrackQueueService.addAll(
                                                context.getGuild(),
                                                TrackQueueElement.listOf(
                                                        playlistLoaded.getTracks(),
                                                        context.getEvent().getInteraction().getUser()
                                                )
                                        )))
                                .and(Mono.justOrEmpty(event.getMessage()).flatMap(TimeoutUtils::clearActionBar))
                )
                .build()
                .getMessage();

        Mono<Void> playMono = Mono.empty();
        if(playlistLoaded.getInfo().getSelectedTrack() != -1)
            playMono = playTrackService.playTrack(
                    context.getGuild(),
                    context.getChannel(),
                    context.getEvent().getInteraction(),
                    playlistLoaded.getTracks().get(playlistLoaded.getInfo().getSelectedTrack()));

        return playMono.then(context.getEvent().createFollowup(shortPlaylistMessage.asFollowupSpec())
                .flatMap(message -> shortPlaylistMessage.getResponseInteraction().get().interaction(message)));
    }
}
