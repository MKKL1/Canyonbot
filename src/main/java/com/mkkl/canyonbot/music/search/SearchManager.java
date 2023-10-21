package com.mkkl.canyonbot.music.search;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.lava.common.tools.DaemonThreadFactory;
import com.sedmelluq.lava.common.tools.ExecutorTools;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class SearchManager {

    private static final int DEFAULT_LOADER_POOL_SIZE = 10;
    private static final int LOADER_QUEUE_CAPACITY = 5000;

    private final SourceRegistry sourceRegistry;
    private final ThreadPoolExecutor trackInfoExecutorService;
    public SearchManager(SourceRegistry sourceRegistry) {
        this.sourceRegistry = sourceRegistry;
        trackInfoExecutorService = ExecutorTools.createEagerlyScalingExecutor(1, DEFAULT_LOADER_POOL_SIZE,
                TimeUnit.SECONDS.toMillis(30), LOADER_QUEUE_CAPACITY, new DaemonThreadFactory("info-loader"));
    }

    public AudioItem search(String query, AudioSourceManager sourceManager) {
        return sourceManager.loadItem(null, new AudioReference(query, null));
    }

    public Mono<SearchResult> searchSync(String query) {
        for(AudioSourceManager sourceManager : sourceRegistry.getSourceList()) {
            AudioItem item = search(query, sourceManager);
            if(item != null) {
                if (item instanceof AudioTrack) {
                    return Mono.just(new SearchResult(null, List.of((AudioTrack) item)));
                } else if (item instanceof AudioPlaylist) {
                    return Mono.just(new SearchResult(List.of((AudioPlaylist) item), null));
                }
                return Mono.error(new RuntimeException("Unknown audio item type"));
            }
        }
        return Mono.error(new RuntimeException("No source manager found for query"));
    }

    public Mono<SearchResult> search(String query) {
        return Mono.fromCallable(() -> trackInfoExecutorService.submit(() -> searchSync(query)).get().block());//TODO async
    }




}
