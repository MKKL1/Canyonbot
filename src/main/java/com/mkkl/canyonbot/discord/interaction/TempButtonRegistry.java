package com.mkkl.canyonbot.discord.interaction;

import com.austinv11.servicer.Service;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TempButtonRegistry {
    public Map<String, TempListenableButton> buttonMap = new ConcurrentHashMap<>();
    public TempButtonRegistry(DiscordClient client) {
        client.withGateway(gateway -> gateway.on(ButtonInteractionEvent.class)
                .filter(event -> buttonMap.containsKey(event.getCustomId()))
                .flatMap(event -> Mono.zip(Mono.just(event), Mono.just(buttonMap.get(event.getCustomId()))))
                .flatMap(tuple -> tuple.getT2().getButtonClickAction().apply(tuple.getT1()))
        ).subscribe();
    }
}
