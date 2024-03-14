package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.event.LinkContextCreationEvent;
import com.mkkl.canyonbot.music.player.event.LinkContextDestroyEvent;
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
    private final EventDispatcher eventDispatcher;

    public LinkContextRegistry(LavalinkClient lavalinkClient, EventDispatcher eventDispatcher) {
        this.lavalinkClient = lavalinkClient;
        this.eventDispatcher = eventDispatcher;
    }

    public LinkContext getOrCreate(Guild guild) {
        //TODO move this to a factory
        //TODO handle no lavalink nodes available
        if(lavalinkClient.getNodes().isEmpty()) throw new RuntimeException("No lavalink nodes!");
        LinkContext linkContext = linkContextMap.computeIfAbsent(guild,
                g -> new LinkContext(g, lavalinkClient.getOrCreateLink(g.getId().asLong()), eventDispatcher)
        );
        eventDispatcher.publish(new LinkContextCreationEvent(guild.getId().asLong()));
        return linkContext;
    }

    public Optional<LinkContext> getCached(Guild guild) {
        return Optional.ofNullable(linkContextMap.get(guild));
    }

    public boolean isCached(Guild guild) {
        return linkContextMap.containsKey(guild);
    }
    public Mono<Void> destroy(Guild guild) {
        return Optional.ofNullable(linkContextMap.remove(guild))
                .map(LinkContext::destroy)
                .orElse(Mono.empty())
                .then(Mono.fromRunnable(() -> eventDispatcher.publish(new LinkContextDestroyEvent(guild.getId().asLong()))));
    }
}
