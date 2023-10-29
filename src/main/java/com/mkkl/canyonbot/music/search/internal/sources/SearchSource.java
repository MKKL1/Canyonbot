package com.mkkl.canyonbot.music.search.internal.sources;

import com.mkkl.canyonbot.music.search.SearchResult;

public interface SearchSource {
    SearchResult search(String query);
    default String name() {
        return null;
    }
    String identifier();
}
