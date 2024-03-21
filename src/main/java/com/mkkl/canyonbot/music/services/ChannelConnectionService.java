package com.mkkl.canyonbot.music.services;

import dev.arbjerg.lavalink.libraries.discord4j.Discord4JUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.AudioChannel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChannelConnectionService {

    private final GatewayDiscordClient gatewayDiscordClient;

    public ChannelConnectionService(GatewayDiscordClient gatewayDiscordClient) {
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

    public Mono<Void> join(AudioChannel audioChannel) {
        return audioChannel.sendConnectVoiceState(false, false);
    }

    public Mono<Void> leave(Snowflake guildId) {
        return Discord4JUtils.leave(gatewayDiscordClient, guildId);
    }
}
