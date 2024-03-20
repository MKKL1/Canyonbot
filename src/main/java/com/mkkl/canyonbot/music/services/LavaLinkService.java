package com.mkkl.canyonbot.music.services;

import com.mkkl.canyonbot.music.player.LinkContext;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.protocol.Track;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LavaLinkService {
    private final LinkContextRegistry linkContextRegistry;

    public LavaLinkService(LinkContextRegistry linkContextRegistry) {
        this.linkContextRegistry = linkContextRegistry;
    }

    public Mono<Void> playTrack(Guild guild, Track track) {
        LinkContext linkContext = linkContextRegistry.getOrCreate(guild);
        return linkContext.getLink().getPlayer().flatMap(player -> player.setTrack(track)).then();
    }
}
