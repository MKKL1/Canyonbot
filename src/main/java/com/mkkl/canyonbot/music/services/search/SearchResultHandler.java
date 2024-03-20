package com.mkkl.canyonbot.music.services.search;

import com.mkkl.canyonbot.music.commands.PlayCommand;
import com.mkkl.canyonbot.music.messages.generators.AudioTrackMessage;
import com.mkkl.canyonbot.music.services.PlayTrackService;
import dev.arbjerg.lavalink.client.protocol.SearchResult;
import dev.arbjerg.lavalink.client.protocol.TrackLoaded;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SearchResultHandler implements LavalinkLoadResultHandler<SearchResult> {
    @Autowired
    private TrackResultHandler trackResultHandler;
    @Override
    public Mono<?> handle(PlayCommand.Context context, SearchResult searchResult) {
        return trackResultHandler.handleTrack(context, searchResult.getTracks()
                .getFirst());
    }
}
