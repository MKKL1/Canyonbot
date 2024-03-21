package com.mkkl.canyonbot.music.services.search;

import com.mkkl.canyonbot.music.exceptions.NoMatchException;
import com.mkkl.canyonbot.music.search.SearchQuery;
import com.mkkl.canyonbot.music.search.SourceRegistry;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.lava.common.tools.DaemonThreadFactory;
import com.sedmelluq.lava.common.tools.ExecutorTools;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.protocol.LavalinkLoadResult;
import discord4j.core.object.entity.Guild;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SearchService {
    private final LavalinkClient lavalinkClient;
    public SearchService(LavalinkClient lavalinkClient) {
        this.lavalinkClient = lavalinkClient;
    }

    //TODO retry with youtube search if search with link didn't work
    public Mono<LavalinkLoadResult> search(Guild guild, String identifier) {
        return lavalinkClient.getOrCreateLink(guild.getId().asLong())
                .loadItem(identifier)
                .doOnNext(lavalinkLoadResult ->
                        log.info("[g:" + guild.getId().asLong() + "] Search: " + identifier));
    }

    public Mono<LavalinkLoadResult> search(Guild guild, String identifier, SearchSource searchSource) {
        return search(guild, SearchQuery.builder().source(searchSource).identifier(identifier).build().getQuery());
    }

}
