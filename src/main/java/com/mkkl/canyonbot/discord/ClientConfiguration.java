package com.mkkl.canyonbot.discord;

import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.event.lavalink.LavalinkEventAdapter;
import dev.arbjerg.lavalink.client.ClientEvent;
import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.gateway.intent.IntentSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Configuration
public class ClientConfiguration {
    private final String token;

    public ClientConfiguration() {
        token = System.getenv("DISCORD_BOT_TOKEN");
        if(token == null || token.isBlank() || token.isEmpty()) {
            log.error("Couldn't find token");
            throw new RuntimeException("Token is undefined");
        }
        Pattern pattern = Pattern.compile("/(mfa\\.[\\w-]{84}|[\\w-]{24}\\.[\\w-]{6}\\.[\\w-]{27})/");
        if(pattern.matcher(token).matches()) {
            log.error("Invalid token");
            throw new RuntimeException("Token is invalid");
        }
    }
    public long getUserId() {
        return Helpers.getUserIdFromToken(token);
    }

    @Bean
    public GatewayDiscordClient gateway() {
        return DiscordClientBuilder
                .create(token)
                .build()
                .gateway()
                .setEnabledIntents(IntentSet.nonPrivileged())
                .login()
                .block();
    }
}
