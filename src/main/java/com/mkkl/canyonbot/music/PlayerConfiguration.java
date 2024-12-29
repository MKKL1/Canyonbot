package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.discord.ClientConfiguration;
import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.event.lavalink.LavalinkEventAdapter;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.event.ClientEvent;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Slf4j
@EnableConfigurationProperties(value = PlayerConfigurationProperties.class)
@Configuration
public class PlayerConfiguration {

    private final PlayerConfigurationProperties properties;

    private final ClientConfiguration clientConfiguration;
    private final EventDispatcher eventDispatcher;

    public PlayerConfiguration(PlayerConfigurationProperties properties, ClientConfiguration clientConfiguration, EventDispatcher eventDispatcher) {
        this.properties = properties;
        this.clientConfiguration = clientConfiguration;
        this.eventDispatcher = eventDispatcher;
    }

    @Bean
    public LavalinkClient lavalinkClient() {
        LavalinkClient client = new LavalinkClient(clientConfiguration.getUserId());
        client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        //TODO implement priority for servers (eg. first use local server then external)
        if(properties.nodes == null) throw new RuntimeException("No nodes were defined");
        properties.nodes.forEach(nodeOptions -> {
            log.info("Adding node " + nodeOptions.name());
            client.addNode(new NodeOptions.Builder()
                    .setName(nodeOptions.name())
                    .setServerUri(nodeOptions.uri())
                    .setPassword(nodeOptions.password())
                    .setHttpTimeout(nodeOptions.httpTimeout())
                    .build());
        });
        client.on(ClientEvent.class).subscribe(event -> eventDispatcher.publish(LavalinkEventAdapter.get(event)));
        return client;

    }
}
