package com.mkkl.canyonbot.music.search.handler;

import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.music.commands.PlayCommand;
import com.mkkl.canyonbot.music.messages.data.AudioTrackMessage;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.client.player.TrackLoaded;
import org.springframework.stereotype.Service;

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
