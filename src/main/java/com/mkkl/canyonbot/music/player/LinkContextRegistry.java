package com.mkkl.canyonbot.music.player;

import dev.arbjerg.lavalink.client.LavalinkClient;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LinkContextRegistry {
    private final LavalinkClient lavalinkClient;
    private final Map<Guild, LinkContext> linkContextMap = new ConcurrentHashMap<>();

    public LinkContextRegistry(LavalinkClient lavalinkClient) {
        this.lavalinkClient = lavalinkClient;
    }

    public LinkContext getOrCreate(Guild guild) {
        //TODO move this to a factory
        return linkContextMap.computeIfAbsent(guild, g -> new LinkContext(g, lavalinkClient.getOrCreateLink(g.getId().asLong())));
    }

    public Optional<LinkContext> getCached(Guild guild) {
        return Optional.ofNullable(linkContextMap.get(guild));
    }

    public Mono<Void> destroy(Guild guild) {
        return Optional.ofNullable(linkContextMap.remove(guild))
                .map(LinkContext::destroy)
                .orElse(Mono.empty());
    }
}
