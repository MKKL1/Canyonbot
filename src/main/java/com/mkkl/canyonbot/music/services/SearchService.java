package com.mkkl.canyonbot.music.services;

import com.mkkl.canyonbot.music.search.SearchQuery;
import com.mkkl.canyonbot.music.search.sources.SearchSource;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.LavalinkLoadResult;
import discord4j.core.object.entity.Guild;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SearchService {
    private final LavalinkClient lavalinkClient;
    public SearchService(LavalinkClient lavalinkClient) {
        this.lavalinkClient = lavalinkClient;
    }

    //TODO retry with youtube search if search with link didn't work
    public Mono<LavalinkLoadResult> search(Guild guild, String identifier) {
        //TODO searching doesn't need whole LinkContext, just normal Link. It is possible that this Link won't be cleaned up
        return lavalinkClient.getOrCreateLink(guild.getId().asLong())
                .loadItem(identifier)
                .doOnNext(lavalinkLoadResult ->
                        log.info("[g:" + guild.getId().asLong() + "] Search: " + identifier + " result: " + lavalinkLoadResult));
    }

    public Mono<LavalinkLoadResult> search(Guild guild, String identifier, SearchSource searchSource) {
        return search(guild, SearchQuery.builder().source(searchSource).identifier(identifier).build().getQuery());
    }

}
