package com.mkkl.canyonbot.music.services;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LavaLinkService {
    private final LavalinkClient lavalinkClient;
    public LavaLinkService(LavalinkClient lavalinkClient) {
        this.lavalinkClient = lavalinkClient;
    }

    public Mono<Void> playTrack(Guild guild, ) {
        Link link = lavalinkClient.getOrCreateLink(guild.getId().asLong());
        link.getPlayer()
                .flatMap(lavalinkPlayer -> lavalinkPlayer.setIdentifier())
    }
}
