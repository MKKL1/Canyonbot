package com.mkkl.canyonbot.music.player.queue;

import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.AudioChannelJoinSpec;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import jakarta.annotation.Nullable;
import reactor.core.publisher.Mono;

//Interface to manage the queue with all checks (join channel, ...)
public class VoiceConnectionQueueManager {
    private final TrackQueue<TrackQueueElement> trackQueue = new SimpleTrackQueue();
    private final AudioProvider audioProvider;

    @Nullable
    private VoiceConnection voiceConnection = null;

    public VoiceConnectionQueueManager(AudioProvider audioProvider) {
        this.audioProvider = audioProvider;
    }

    public Mono<Void> enqueueAndJoin(TrackQueueElement track, VoiceChannel voiceChannel) {
        Mono<Void> mono = Mono.empty();
        if(voiceConnection == null) {
            mono = voiceChannel.join(AudioChannelJoinSpec.builder().provider(audioProvider).build()).flatMap(vc -> {
                voiceConnection = vc;
                return Mono.empty();
            });
        }
        return mono.then(Mono.fromRunnable(() -> trackQueue.enqueue(track)));
    }

    public Mono<TrackQueueElement> dequeue() {
        TrackQueueElement track = trackQueue.dequeue();
        if(track == null && voiceConnection != null) {
            return voiceConnection.disconnect().then(Mono.fromRunnable(() -> voiceConnection = null));
        }
        if(track == null) Mono.error(new IllegalStateException("No track to dequeue"));
        return Mono.just(track);
    }
}
