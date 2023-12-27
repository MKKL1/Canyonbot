package com.mkkl.canyonbot.discord;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.AudioChannel;
import discord4j.core.spec.AudioChannelJoinSpec;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GuildVoiceConnectionService {
    private final Map<Guild, VoiceConnection> voiceConnections = new ConcurrentHashMap<>();

    //TODO check changing channels
    public Mono<VoiceConnection> join(Guild guild, AudioProvider audioProvider, AudioChannel audioChannel) {
        return isConnected(guild)
                .filter(isConnected -> isConnected)
                .then(audioChannel.join(AudioChannelJoinSpec.builder()
                                .provider(audioProvider)
                                .build())
                        .doOnNext(voiceConnection -> voiceConnections.put(guild, voiceConnection)));
    }

    public Mono<Void> leave(Guild guild) {
        return Mono.justOrEmpty(getVoiceConnection(guild))
                .switchIfEmpty(Mono.error(new IllegalStateException("Not connected to a voice channel")))
                .flatMap(VoiceConnection::disconnect)
                .then(Mono.fromRunnable(() -> voiceConnections.remove(guild)));
    }

    private Optional<VoiceConnection> getVoiceConnection(Guild guild) {
        return Optional.ofNullable(voiceConnections.get(guild));
    }

    public Mono<Boolean> isConnected(Guild guild) {
        return getVoiceConnection(guild)
                .map(VoiceConnection::isConnected)
                .orElseGet(() -> Mono.just(false));
    }
}
