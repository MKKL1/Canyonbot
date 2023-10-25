package com.mkkl.canyonbot.music.search.internal.sources.youtube;

import com.mkkl.canyonbot.music.search.internal.sources.RegisterSource;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;

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
    public AudioItem search(String query) {
        //This is very lazy solution but lavaplayer's way of identifying route is also lazy, so I don't care
        return sourceManager.loadItem(null, new AudioReference(route + query, null));
    }
}
