package com.mkkl.canyonbot.discord;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Configuration
public class ClientConfiguration {

    static Logger logger = LoggerFactory.getLogger(ClientConfiguration.class);
    private final String token = System.getenv("DISCORD_BOT_TOKEN");
    @Bean
    public DiscordClient discordClient() {
        if(token == null || token.isBlank() || token.isEmpty()) {
            logger.error("Couldn't find token");
            throw new RuntimeException("Token is undefined");
        }
        Pattern pattern = Pattern.compile("/(mfa\\.[\\w-]{84}|[\\w-]{24}\\.[\\w-]{6}\\.[\\w-]{27})/");
        if(pattern.matcher(token).matches()) {
            logger.error("Invalid token");
            throw new RuntimeException("Token is invalid");
        }
        return DiscordClientBuilder.create(token).build();
    }

    @Bean
    public Mono<GatewayDiscordClient> gatewayDiscordClient() {
        return discordClient().login();
    }
}
