package com.mkkl.canyonbot.music.services.search;

import com.mkkl.canyonbot.music.commands.PlayCommand;
import com.mkkl.canyonbot.music.search.SearchResult;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface SearchResultHandler {
    Mono<?> handle(PlayCommand.Context context, SearchResult searchResult);
}
