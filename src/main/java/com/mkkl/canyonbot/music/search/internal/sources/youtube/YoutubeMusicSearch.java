package com.mkkl.canyonbot.music.search.internal.sources.youtube;

import com.mkkl.canyonbot.music.search.SearchResult;
import com.mkkl.canyonbot.music.search.internal.sources.RegisterSource;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

//This is almost the same as YoutubeSearch, only difference is route string.
//Which means it could be merged into one class, but I don't want to overcomplicate things
@RegisterSource(priority = 0)
public class YoutubeMusicSearch implements SearchSource {
    private final YoutubeAudioSourceManager sourceManager;
    public static final String route = "ytmsearch:";
    public YoutubeMusicSearch(YoutubeAudioSourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    @Override
    public SearchResult search(String query) {
        return YoutubeCommon.searchFromRoute(sourceManager, route, query, this);
    }

    @Override
    public String name() {
        return "YoutubeMusic search";
    }

    @Override
    public String identifier() {
        return "ytm";
    }

    @Override
    public String logoUrl() {
        return YoutubeCommon.musicLogoUrl;
    }
}
