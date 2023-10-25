package com.mkkl.canyonbot.music.search.internal.sources.youtube;

import com.mkkl.canyonbot.music.search.internal.sources.RegisterSource;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;

@RegisterSource(priority = 1)
public class YoutubeSearch implements SearchSource {

    private final YoutubeAudioSourceManager sourceManager;
    public static final String route = "ytsearch:";
    public YoutubeSearch(YoutubeAudioSourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    @Override
    public AudioItem search(String query) {
        //This is very lazy solution but lavaplayer's way of identifying route is also lazy, so I don't care
        return sourceManager.loadItem(null, new AudioReference(route + query, null));
    }
}
