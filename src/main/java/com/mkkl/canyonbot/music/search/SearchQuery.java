package com.mkkl.canyonbot.music.search;

import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import lombok.Builder;

@Builder
public class SearchQuery {
    private SearchSource source;
    private String identifier;

    public String getQuery() {
        return source.searchIdentifier() + ":\"" + identifier + "\"";
    }
}
