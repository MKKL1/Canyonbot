package com.mkkl.canyonbot;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class CanyonbotApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(CanyonbotApplication.class, args);
        String[] beanNamesForType = context.getBeanNamesForType(ResolvableType.forClassWithGenerics(Mono.class, GatewayDiscordClient.class));
        Mono<GatewayDiscordClient> gatewayDiscordClientMono = (Mono<GatewayDiscordClient>) (context.getBean(beanNamesForType[0]));
        gatewayDiscordClientMono.flatMap(GatewayDiscordClient::onDisconnect).subscribe();
    }

}
