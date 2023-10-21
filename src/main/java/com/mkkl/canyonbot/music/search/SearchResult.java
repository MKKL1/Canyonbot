package com.mkkl.canyonbot.music.search;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.annotation.Nullable;
import lombok.Getter;

import java.util.List;

public class SearchResult {

    @Getter
    @Nullable
    private final List<AudioPlaylist> playlists;
    @Getter
    @Nullable
    private final List<AudioTrack> tracks;

    public SearchResult(@Nullable List<AudioPlaylist> playlists,
                        @Nullable List<AudioTrack> tracks) {
        this.playlists = playlists;
        this.tracks = tracks;
    }
}
