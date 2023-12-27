package com.mkkl.canyonbot.discord.interaction;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public abstract class ActionButton {
    @Getter
    private final String id;
    private final Mono<GatewayDiscordClient> gatewayDiscordClientMono;
    protected ActionButton(String id, Mono<GatewayDiscordClient> gatewayDiscordClientMono) {
        this.id = id;
        this.gatewayDiscordClientMono = gatewayDiscordClientMono;
    }
    public Flux<ButtonInteractionEvent> onInteraction() {
        return gatewayDiscordClientMono.flatMapMany(gateway ->
                gateway.on(ButtonInteractionEvent.class)
                        .filter(event -> event.getCustomId().equals(id))
        );
    }
}
