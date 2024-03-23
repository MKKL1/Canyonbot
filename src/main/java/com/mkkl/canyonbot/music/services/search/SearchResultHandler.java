package com.mkkl.canyonbot.music.services.search;

import com.mkkl.canyonbot.music.commands.PlayCommand;
import dev.arbjerg.lavalink.client.protocol.SearchResult;
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
