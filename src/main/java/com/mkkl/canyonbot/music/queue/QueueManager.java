package com.mkkl.canyonbot.music.queue;

import com.mkkl.canyonbot.discord.VoiceChannelManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

//Interface to manage the queue with all checks (join channel, ...)
@Component
public class QueueManager {
    private final TrackQueue<TrackQueueElement> trackQueue = new SimpleTrackQueue();
    private final VoiceChannelManager voiceChannelManager;
    private final AudioProvider audioProvider;

    public QueueManager(VoiceChannelManager voiceChannelManager, AudioProvider audioProvider) {
        this.voiceChannelManager = voiceChannelManager;
        this.audioProvider = audioProvider;
    }

    public Mono<Void> enqueueAndJoin(TrackQueueElement track, VoiceChannel voiceChannel) {
        Mono<Void> mono = Mono.empty();
        if(!voiceChannelManager.isConnected()) {
            mono = voiceChannelManager.joinChannel(voiceChannel, audioProvider);
        }
        return mono.then(Mono.fromRunnable(() -> trackQueue.enqueue(track)));
    }

    @Bean
    public TrackQueue<TrackQueueElement> trackQueue() {
        return trackQueue;
    }
}
