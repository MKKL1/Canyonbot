package com.mkkl.canyonbot.music.search.internal.sources;

import com.mkkl.canyonbot.commands.completion.SuggestionOption;

public class SourceSuggestionOption implements SuggestionOption {

    private final SearchSource source;

    public SourceSuggestionOption(SearchSource source) {
        this.source = source;
    }

    @Override
    public long getWeight() {
        return 0;
    }

    @Override
    public String getName() {
        return source.name();
    }

    @Override
    public String getDescription() {
        return null;//TODO
    }

    @Override
    public String[] getContexts() {
        return source.autoCompleteAliases();
    }
}
