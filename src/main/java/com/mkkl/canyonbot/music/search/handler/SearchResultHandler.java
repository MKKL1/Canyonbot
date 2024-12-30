package com.mkkl.canyonbot.music.search.handler;

import com.mkkl.canyonbot.music.commands.PlayCommand;
import dev.arbjerg.lavalink.client.player.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchResultHandler implements LavalinkLoadResultHandler<SearchResult> {
    @Autowired
    private TrackResultHandler trackResultHandler;
    @Override
    public ResultHandlerResponse handle(PlayCommand.Context context, SearchResult searchResult) {
        //Assume the first track is the one we want
        //In future we could show a list of tracks to choose from (probably not needed)
        return trackResultHandler.handleTrack(context, searchResult.getTracks().getFirst());
    }
}
