package com.mkkl.canyonbot.music.buttons;

import discord4j.core.GatewayDiscordClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class NextPageButton extends ActionButton {
    public NextPageButton(Mono<GatewayDiscordClient> gatewayDiscordClientMono) {
        super("queue_next_page", gatewayDiscordClientMono);
    }
}
