package com.mkkl.canyonbot.discord;

import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.AudioChannelJoinSpec;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import lombok.Getter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Getter
@Component
public class VoiceChannelManager {

    private Optional<VoiceConnection> voiceConnection = Optional.empty();

    public Mono<Void> joinChannel(VoiceChannel voiceChannel, AudioProvider audioProvider) {
        return voiceChannel.join(AudioChannelJoinSpec.builder().provider(audioProvider).build()).flatMap(connection -> {
            voiceConnection = Optional.of(connection);
            return Mono.empty();
        });
    }

    public Mono<Void> leaveChannel() {
        return voiceConnection.map(VoiceConnection::disconnect).orElse(Mono.empty());
    }

    public boolean isConnected() {
        return voiceConnection.isPresent();
    }
}
