package com.mkkl.canyonbot.music.buttons;

import discord4j.core.GatewayDiscordClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PlaylistAddAllButton extends ActionButton {
    public PlaylistAddAllButton(Mono<GatewayDiscordClient> gatewayDiscordClient) {
        super("playlistAddAll", gatewayDiscordClient);
    }
}
