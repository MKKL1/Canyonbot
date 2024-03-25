package com.mkkl.canyonbot.music.services;

import com.mkkl.canyonbot.music.VoiceConnectionRegistry;
import dev.arbjerg.lavalink.libraries.discord4j.Discord4JUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.AudioChannel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChannelConnectionService {

    private final GatewayDiscordClient gatewayDiscordClient;
    private final VoiceConnectionRegistry voiceConnectionRegistry;

    public ChannelConnectionService(GatewayDiscordClient gatewayDiscordClient, VoiceConnectionRegistry voiceConnectionRegistry) {
        this.gatewayDiscordClient = gatewayDiscordClient;
        this.voiceConnectionRegistry = voiceConnectionRegistry;
    }

    public Mono<Void> join(AudioChannel audioChannel) {
        return Mono.fromCallable(() -> voiceConnectionRegistry.isSet(audioChannel.getGuildId().asLong()))
                .filter(isSet -> !isSet)
                .flatMap(ignore -> audioChannel.sendConnectVoiceState(false, false))
                .then(Mono.fromRunnable(() -> voiceConnectionRegistry.remove(audioChannel.getGuildId().asLong())));
    }

    public Mono<Void> leave(Snowflake guildId) {
        return Discord4JUtils.leave(gatewayDiscordClient, guildId)
                .then(Mono.fromRunnable(() -> voiceConnectionRegistry.remove(guildId.asLong())));
    }
}
