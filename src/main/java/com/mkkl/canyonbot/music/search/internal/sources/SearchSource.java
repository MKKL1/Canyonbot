package com.mkkl.canyonbot.music.search.internal.sources;

import com.mkkl.canyonbot.music.search.SearchResult;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;

public interface SearchSource {
    SearchResult search(String query);
}
