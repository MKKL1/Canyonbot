package com.mkkl.canyonbot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceServerUpdateEvent;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
public class CanyonbotApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(CanyonbotApplication.class, args);
        context.getBean(GatewayDiscordClient.class).onDisconnect().block();
    }

}
