package com.mkkl.canyonbot.music.search.internal.handler;

import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.music.commands.PlayCommand;
import com.mkkl.canyonbot.music.messages.generators.AudioTrackMessage;
import com.mkkl.canyonbot.music.services.PlayerService;
import dev.arbjerg.lavalink.client.protocol.Track;
import dev.arbjerg.lavalink.client.protocol.TrackLoaded;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TrackResultHandler implements LavalinkLoadResultHandler<TrackLoaded> {
    @Override
    public ResultHandlerResponse handle(PlayCommand.Context context, TrackLoaded trackLoaded) {
        return handleTrack(context, trackLoaded.getTrack());
    }

    public ResultHandlerResponse handleTrack(PlayCommand.Context context, Track track) {
        Response response = AudioTrackMessage.builder()
                .audioTrack(track)
//                                .source(searchResult.getSource())
                .query(context.getQuery().orElseThrow(() -> new IllegalStateException("Query not found")))
                .user(context.getEvent().getInteraction().getUser())
                .build()
                .getMessage();
        return new ResultHandlerResponse(response, track);
    }
}
