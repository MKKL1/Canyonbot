package com.mkkl.canyonbot.music.buttons;

import discord4j.core.GatewayDiscordClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PrevPageButton extends ActionButton {
    public PrevPageButton(Mono<GatewayDiscordClient> gatewayDiscordClientMono) {
        super("queue_prev_page", gatewayDiscordClientMono);
    }
}
