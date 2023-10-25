package com.mkkl.canyonbot.music.search.internal.sources.youtube;

import com.mkkl.canyonbot.music.search.internal.sources.RegisterSource;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import org.springframework.stereotype.Component;

@RegisterSource(priority = 100)
public class YoutubeLinkSearch implements SearchSource {
    private final YoutubeAudioSourceManager sourceManager;

    public YoutubeLinkSearch(YoutubeAudioSourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    @Override
    public AudioItem search(String query) {
        return sourceManager.loadItem(null, new AudioReference(
                query
                        .replace(YoutubeMusicSearch.route, "")
                        .replace(YoutubeSearch.route, ""),
                null));
    }
}
