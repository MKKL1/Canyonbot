package com.mkkl.canyonbot.music.search;

import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

public class SearchResult {

    @Getter
    @Nullable
    private final List<AudioPlaylist> playlists;
    @Getter
    @Nullable
    private final List<AudioTrack> tracks;
    @Getter
    private final SearchSource source;

    private SearchResult(@Nullable List<AudioPlaylist> playlists,
                         @Nullable List<AudioTrack> tracks,
                         @NonNull SearchSource source) {
        this.playlists = playlists;
        this.tracks = tracks;
        this.source = source;
    }

    public SearchResult create(@Nullable List<AudioPlaylist> playlists,
                               @Nullable List<AudioTrack> tracks,
                               @NonNull SearchSource source) {
        return new SearchResult(playlists, tracks, source);
    }

    public static SearchResult empty(@NonNull SearchSource source) {
        return new SearchResult(null, null, source);
    }

    public static SearchResult fromPlaylists(@Nullable List<AudioPlaylist> playlists,
                                             @NonNull SearchSource source) {
        return new SearchResult(playlists, null, source);
    }

    public static SearchResult fromPlaylist(@Nonnull AudioPlaylist playlist,
                                            @NonNull SearchSource source) {
        return new SearchResult(List.of(playlist), null, source);
    }

    public static SearchResult fromTracks(@Nullable List<AudioTrack> tracks,
                                          @NonNull SearchSource source) {
        return new SearchResult(null, tracks, source);
    }

    public static SearchResult fromTrack(@Nonnull AudioTrack track,
                                         @NonNull SearchSource source) {
        return new SearchResult(null, List.of(track), source);
    }

    public boolean hasPlaylists() {
        return playlists != null && !playlists.isEmpty();
    }

    public boolean hasTracks() {
        return tracks != null && !tracks.isEmpty();
    }

    public boolean isEmpty() {
        return !hasPlaylists() && !hasTracks();
    }
}
