package com.mkkl.canyonbot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CanyonbotApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(CanyonbotApplication.class, args);
        context.getBean(DiscordClient.class).withGateway(GatewayDiscordClient::onDisconnect).subscribe();
    }

}
