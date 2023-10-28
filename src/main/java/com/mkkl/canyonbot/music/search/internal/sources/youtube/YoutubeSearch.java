package com.mkkl.canyonbot.music.search.internal.sources.youtube;

import com.mkkl.canyonbot.commands.completion.SuggestionOption;
import com.mkkl.canyonbot.music.search.SearchResult;
import com.mkkl.canyonbot.music.search.internal.sources.RegisterSource;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

@RegisterSource(priority = 1)
public class YoutubeSearch implements SearchSource {

    private final YoutubeAudioSourceManager sourceManager;
    public static final String route = "ytsearch:";
    public YoutubeSearch(YoutubeAudioSourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    @Override
    public SearchResult search(String query) {
        //TODO doesn't search for playlists
        return YoutubeCommon.searchFromRoute(sourceManager, route, query, this);
    }

    @Override
    public String name() {
        return "yt";
    }

    @Override
    public String[] autoCompleteAliases() {
        return new String[] {"youtube", "yt", "search", "music"};
    }
}
