package com.mkkl.canyonbot.music.buttons;

import com.mkkl.canyonbot.discord.interaction.ActionButton;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PlaylistAddAllButton extends ActionButton {
    public PlaylistAddAllButton(Mono<GatewayDiscordClient> gatewayDiscordClient) {
        super("playlistAddAll", gatewayDiscordClient);
    }
}
