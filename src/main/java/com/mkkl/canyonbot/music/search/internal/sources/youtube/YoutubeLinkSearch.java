package com.mkkl.canyonbot.music.search.internal.sources.youtube;

import com.mkkl.canyonbot.music.search.SearchResult;
import com.mkkl.canyonbot.music.search.internal.sources.RegisterSource;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.springframework.stereotype.Component;

@RegisterSource(priority = 100)
public class YoutubeLinkSearch implements SearchSource {
    private final YoutubeAudioSourceManager sourceManager;

    public YoutubeLinkSearch(YoutubeAudioSourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    @Override
    public SearchResult search(String query) {
        AudioItem item = sourceManager.loadItem(null, new AudioReference(stripRoutePrefixes(query), null));
        if(item == null) return SearchResult.empty(this);
        if(item instanceof AudioPlaylist)
            return SearchResult.fromPlaylist((AudioPlaylist) item, this);
        if(item instanceof AudioTrack)
            return SearchResult.fromTrack((AudioTrack) item, this);
        return SearchResult.empty(this);//TODO log this
    }

    private String stripRoutePrefixes(String query) {
        if(query.startsWith(YoutubeMusicSearch.route))
            query = query.substring(YoutubeMusicSearch.route.length());
        if(query.startsWith(YoutubeSearch.route))
            query = query.substring(YoutubeSearch.route.length());
        return query;
    }
}
