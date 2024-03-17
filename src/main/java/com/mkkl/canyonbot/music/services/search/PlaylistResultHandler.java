package com.mkkl.canyonbot.music.services.search;

import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.utils.TimeoutUtils;
import com.mkkl.canyonbot.music.commands.PlayCommand;
import com.mkkl.canyonbot.music.messages.generators.ShortPlaylistMessage;
import com.mkkl.canyonbot.music.player.LinkContext;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.services.PlayTrackService;
import dev.arbjerg.lavalink.client.protocol.PlaylistLoaded;
import discord4j.core.GatewayDiscordClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PlaylistResultHandler implements LavalinkLoadResultHandler<PlaylistLoaded> {
    @Autowired
    private PlayTrackService playTrackService;
    @Autowired
    private GatewayDiscordClient gateway;
    @Autowired
    private LinkContextRegistry linkContextRegistry;

    @Override
    public Mono<?> handle(PlayCommand.Context context, PlaylistLoaded playlistLoaded) {
        LinkContext linkContext = linkContextRegistry.getOrCreate(context.getGuild());
        Response shortPlaylistMessage = ShortPlaylistMessage.builder()
                .query(context.getQuery())
                .playlist(playlistLoaded)
                .user(context.getEvent().getInteraction().getUser())
                .gateway(gateway)
                .onPlay(event ->
                        event.reply("Playing " + playlistLoaded.getTracks().size() + " tracks")
                                .and(Mono.fromRunnable(() ->
                                        linkContext.getTrackQueue().addAll(
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
