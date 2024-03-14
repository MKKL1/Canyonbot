package com.mkkl.canyonbot.discord;

import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.event.lavalink.LavalinkEventAdapter;
import dev.arbjerg.lavalink.client.ClientEvent;
import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.libraries.discord4j.D4JVoiceHandler;
import dev.arbjerg.lavalink.libraries.discord4j.Discord4JUtils;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.regex.Pattern;

@Configuration
public class ClientConfiguration {

    static Logger logger = LoggerFactory.getLogger(ClientConfiguration.class);
    @Autowired
    private EventDispatcher eventDispatcher;
    private final String token;

    public ClientConfiguration() {
        token = System.getenv("DISCORD_BOT_TOKEN");
        if(token == null || token.isBlank() || token.isEmpty()) {
            logger.error("Couldn't find token");
            throw new RuntimeException("Token is undefined");
        }
        Pattern pattern = Pattern.compile("/(mfa\\.[\\w-]{84}|[\\w-]{24}\\.[\\w-]{6}\\.[\\w-]{27})/");
        if(pattern.matcher(token).matches()) {
            logger.error("Invalid token");
            throw new RuntimeException("Token is invalid");
        }
    }

    @Bean
    public DiscordClient discordClient() {
        return DiscordClientBuilder.create(token).build();
    }

    //TODO this needs to be in lavalink configuration
    @Bean
    public LavalinkClient lavalinkClient() {
        LavalinkClient client = new LavalinkClient(Helpers.getUserIdFromToken(token));
        discordClient().withGateway(gateway -> {
            D4JVoiceHandler.install(gateway, client);
            return Mono.empty();
        }).subscribe();

        client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());
        client.addNode(new NodeOptions.Builder().setName("Lavalink server 1")
                .setServerUri(URI.create("ws://localhost:2333"))
                .setPassword("youshallnotpass")
                .setHttpTimeout(5000L)
                .build());
        client.on(ClientEvent.class).subscribe(event -> eventDispatcher.publish(LavalinkEventAdapter.get(event)));
        return client;

    }

    @Bean
    public Mono<GatewayDiscordClient> gatewayDiscordClient() {
        return discordClient().login();
    }
}
