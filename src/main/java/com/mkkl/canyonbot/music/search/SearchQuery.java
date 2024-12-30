package com.mkkl.canyonbot.music.search;

import com.mkkl.canyonbot.music.search.sources.SearchSource;
import lombok.Builder;

@Builder
public class SearchQuery {
    private SearchSource source;
    private String identifier;

    public String getQuery() {
        return source.prefix() + identifier;
    }
}
