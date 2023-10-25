package com.mkkl.canyonbot.music.search.internal.sources.youtube;

import com.mkkl.canyonbot.music.search.SearchResult;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class YoutubeCommon {
    @Bean
    public YoutubeAudioSourceManager youtubeAudioSourceManager() {
        return new YoutubeAudioSourceManager(true, null, null);
    }

    public static SearchResult searchFromRoute(YoutubeAudioSourceManager sourceManager,
                                               String route,
                                               String query,
                                               SearchSource source) {
        //This is very lazy solution but lavaplayer's way of identifying route is also lazy, so I don't care
        AudioItem item = sourceManager.loadItem(null, new AudioReference(route + query, null));
        if(item == null) return SearchResult.empty(source);
        if(item instanceof AudioPlaylist)
            return SearchResult.fromTracks(((AudioPlaylist) item).getTracks(), source);
        if(item instanceof AudioTrack)
            return SearchResult.fromTrack((AudioTrack) item, source);
        return SearchResult.empty(source); //TODO log this
    }
}
