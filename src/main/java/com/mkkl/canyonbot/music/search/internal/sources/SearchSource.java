package com.mkkl.canyonbot.music.search.internal.sources;

public interface SearchSource {
    default String name() {
        return null;
    }
    default String logoUrl() {
        return null;
    }
    String prefix();
}
