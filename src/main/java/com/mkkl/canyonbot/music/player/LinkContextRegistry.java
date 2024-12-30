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

    public LinkContextRegistry(LavalinkClient lavalinkClient, EventDispatcher eventDispatcher) {
        this.lavalinkClient = lavalinkClient;
        this.eventDispatcher = eventDispatcher;
    }

    public LinkContext getOrCreate(long guildId) {
        //TODO move this to a factory?
        //TODO handle no lavalink nodes available
        if(lavalinkClient.getNodes().isEmpty()) throw new RuntimeException("No lavalink nodes!");
        return linkContextMap.computeIfAbsent(guildId, g -> {
                    eventDispatcher.publish(new LinkContextCreationEvent(guildId));
                    log.info("LinkContext created for guild: " + guildId);
                    return new LinkContext(g, lavalinkClient.getOrCreateLink(g), eventDispatcher);
                });
    }

    public Optional<LinkContext> getCached(long guildId) {
        return Optional.ofNullable(linkContextMap.get(guildId));
    }

    public boolean isCached(long guildId) {
        return linkContextMap.containsKey(guildId);
    }

    public Mono<Void> destroy(long guildId) {
        return Mono.fromCallable(() -> linkContextMap.remove(guildId))
                .flatMap(LinkContext::destroy)
                .then(Mono.fromRunnable(() -> eventDispatcher.publish(new LinkContextDestroyEvent(guildId))));
    }
}
