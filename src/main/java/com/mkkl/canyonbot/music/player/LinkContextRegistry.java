package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.event.LinkContextCreationEvent;
import com.mkkl.canyonbot.music.player.event.LinkContextDestroyEvent;
import dev.arbjerg.lavalink.client.LavalinkClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class LinkContextRegistry {
    private final LavalinkClient lavalinkClient;
    private final Map<Long, LinkContext> linkContextMap = new ConcurrentHashMap<>();
    private final EventDispatcher eventDispatcher;

    public LinkContextRegistry(LavalinkClient lavalinkClient, EventDispatcher eventDispatcher, GatewayDiscordClient gatewayDiscordClient) {
        this.lavalinkClient = lavalinkClient;
        this.eventDispatcher = eventDispatcher;
    }

    public LinkContext getOrCreate(Guild guild) {
        //TODO move this to a factory
        //TODO handle no lavalink nodes available
        if(lavalinkClient.getNodes().isEmpty()) throw new RuntimeException("No lavalink nodes!");
        LinkContext linkContext = linkContextMap.computeIfAbsent(guild.getId().asLong(),
                g -> new LinkContext(g, lavalinkClient.getOrCreateLink(g), eventDispatcher)
        );
        eventDispatcher.publish(new LinkContextCreationEvent(guild.getId().asLong()));
        return linkContext;
    }

    public Optional<LinkContext> getCached(long guildId) {
        return Optional.ofNullable(linkContextMap.get(guildId));
    }

    public boolean isCached(long guildId) {
        return linkContextMap.containsKey(guildId);
    }
    public Mono<Void> destroy(long guildId) {
        return Optional.ofNullable(linkContextMap.remove(guildId))
                .map(LinkContext::destroy)
                .orElse(Mono.empty())
                .then(Mono.fromRunnable(() -> eventDispatcher.publish(new LinkContextDestroyEvent(guildId))));
    }
}
