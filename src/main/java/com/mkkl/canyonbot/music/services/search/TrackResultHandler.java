package com.mkkl.canyonbot.music.services.search;

import com.mkkl.canyonbot.music.commands.PlayCommand;
import com.mkkl.canyonbot.music.messages.generators.AudioTrackMessage;
import com.mkkl.canyonbot.music.services.PlayTrackService;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.arbjerg.lavalink.client.protocol.TrackLoaded;
import dev.arbjerg.lavalink.protocol.v4.LoadResult;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TrackResultHandler implements SearchResultHandler<TrackLoaded> {
    @Autowired
    private PlayTrackService playTrackService;
    @Override
    public Mono<?> handle(PlayCommand.Context context, TrackLoaded trackLoaded) {
        return playTrackService.playTrack(context.getGuild(), context.getChannel(), context.getEvent().getInteraction(), trackLoaded.getTrack())
                .then(context.getEvent().createFollowup(InteractionFollowupCreateSpec.builder()
                        .addAllEmbeds(AudioTrackMessage.builder()
                                .audioTrack(trackLoaded.getTrack())
//                                .source(searchResult.getSource())
                                .query(context.getQuery().orElseThrow(() -> new IllegalStateException("Query not found")))
                                .user(context.getEvent().getInteraction().getUser())
                                .build()
                                .getMessage().embeds())
                        .build()));
    }
}
