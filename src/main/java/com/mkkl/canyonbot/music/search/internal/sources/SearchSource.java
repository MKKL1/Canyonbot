package com.mkkl.canyonbot.music.search.internal.sources;

import com.mkkl.canyonbot.music.search.SearchResult;

public interface SearchSource {
    SearchResult search(String query);
    String name();
    default String[] autoCompleteAliases() {
        return new String[0];
    }
}
