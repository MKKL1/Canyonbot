package com.mkkl.canyonbot.music.search.internal.handler;

import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.utils.TimeoutUtils;
import com.mkkl.canyonbot.music.commands.PlayCommand;
import com.mkkl.canyonbot.music.messages.generators.ShortPlaylistMessage;
import com.mkkl.canyonbot.music.player.LinkContext;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.services.PlayerService;
import dev.arbjerg.lavalink.client.protocol.PlaylistLoaded;
import dev.arbjerg.lavalink.client.protocol.Track;
import discord4j.core.GatewayDiscordClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.InvalidParameterException;
import java.util.Optional;

@Component
public class PlaylistResultHandler implements LavalinkLoadResultHandler<PlaylistLoaded> {
    @Autowired
    private GatewayDiscordClient gateway;
    @Autowired
    private PlayerService playerService;

    @Override
    public ResultHandlerResponse handle(PlayCommand.Context context, PlaylistLoaded playlistLoaded) {
        Response shortPlaylistMessage = ShortPlaylistMessage.builder()
                .query(context.getQuery())
                .playlist(playlistLoaded)
                .user(context.getEvent().getInteraction().getUser())
                .gateway(gateway)
                .onPlay(event ->
                        event.reply("Playing " + playlistLoaded.getTracks().size() + " tracks")
                                .and(Mono.fromRunnable(() ->
                                        playerService.addTracksToQueue(context.getGuild().getId().asLong(),
                                                TrackQueueElement.listOf(
                                                        playlistLoaded.getTracks(),
                                                        context.getEvent().getInteraction().getUser()
                                                )
                                        )))
                                .and(Mono.justOrEmpty(event.getMessage()).flatMap(TimeoutUtils::clearActionBar))
                )
                .build()
                .getMessage();

        Track track;
        if (playlistLoaded.getInfo().getSelectedTrack() != -1) {
            track = playlistLoaded.getTracks().get(playlistLoaded.getInfo().getSelectedTrack());
        }
        else if(!playlistLoaded.getTracks().isEmpty()) {
            track = playlistLoaded.getTracks().getFirst();
        } else {
            throw new RuntimeException("No tracks found in playlist");//TODO proper exception
        }



        return new ResultHandlerResponse(shortPlaylistMessage, track);
//
//        Mono<Void> playMono = Mono.empty();
//        if(playlistLoaded.getInfo().getSelectedTrack() != -1)
//            playMono = playTrackService.playTrack(
//                    context.getGuild(),
//                    context.getChannel(),
//                    context.getEvent().getInteraction(),
//                    playlistLoaded.getTracks().get(playlistLoaded.getInfo().getSelectedTrack()));
//
//        return playMono.then(context.getEvent().createFollowup(shortPlaylistMessage.asFollowupSpec())
//                .flatMap(message -> shortPlaylistMessage.getResponseInteraction().get().interaction(message)));
    }
}
