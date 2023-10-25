package com.mkkl.canyonbot.music.search.internal.sources.youtube;

import com.mkkl.canyonbot.music.search.SearchResult;
import com.mkkl.canyonbot.music.search.internal.sources.RegisterSource;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

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
}
