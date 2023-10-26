package com.mkkl.canyonbot.music.search;

import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.lava.common.tools.DaemonThreadFactory;
import com.sedmelluq.lava.common.tools.ExecutorTools;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

@Service
public class SearchManager {

    private static final int DEFAULT_LOADER_POOL_SIZE = 10;
    private static final int LOADER_QUEUE_CAPACITY = 5000;

    private final SourceRegistry sourceRegistry;
    private final Scheduler scheduler;
    public SearchManager(SourceRegistry sourceRegistry) {
        this.sourceRegistry = sourceRegistry;
        scheduler = Schedulers.fromExecutorService(
                ExecutorTools.createEagerlyScalingExecutor(1,
                    DEFAULT_LOADER_POOL_SIZE,
                    TimeUnit.SECONDS.toMillis(30),
                    LOADER_QUEUE_CAPACITY,
                    new DaemonThreadFactory("info-loader")
                )
        );
    }
    public Mono<SearchResult> searchSync(String query, SearchSource searchSource) {
        return Mono.just(searchSource.search(query));
    }

    public Mono<SearchResult> searchSync(String query) {
        for(SearchSource sourceManager : sourceRegistry.getSourceList()) {
            SearchResult item = sourceManager.search(query); //TODO replace repeating code
            if(!item.isEmpty()) return Mono.just(item);
        }
        return Mono.error(new RuntimeException("No source manager found for query: " + query));
    }

    public Mono<SearchResult> search(String query) {
        return searchSync(query).subscribeOn(scheduler);
    }

    public Mono<SearchResult> search(String query, SearchSource searchSource) {
        return searchSync(query, searchSource).subscribeOn(scheduler);
    }




}
