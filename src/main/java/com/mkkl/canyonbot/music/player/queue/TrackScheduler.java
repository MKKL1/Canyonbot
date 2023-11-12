package com.mkkl.canyonbot.music.player.queue;

import com.mkkl.canyonbot.music.player.MusicPlayer;
import com.mkkl.canyonbot.music.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import reactor.core.publisher.Mono;

public class TrackScheduler {
    private final TrackQueue<TrackQueueElement> queue;
    private final MusicPlayer musicPlayer;
    public TrackScheduler(TrackQueue<TrackQueueElement> queue, MusicPlayer musicPlayer) {
        this.queue = queue;
        musicPlayer.on(TrackEndEvent.class).flatMap(trackEndEvent -> {
            if (trackEndEvent.getEndReason() == AudioTrackEndReason.FINISHED) {
                return Mono.fromRunnable(() -> {
                    TrackQueueElement track = this.queue.dequeue();
                    if (track != null)
                        musicPlayer.playTrack(track.getAudioTrack());
                    else leaveChannel();
                });
            }
            return Mono.fromRunnable(this::leaveChannel);
        }).subscribe();
        this.musicPlayer = musicPlayer;
    }

    public Mono<Void> start() {
        if(musicPlayer.getPlayingTrack() != null) return Mono.empty();
        return Mono.fromRunnable(() -> {
            TrackQueueElement track = queue.dequeue();
            if (track != null)
                musicPlayer.playTrack(track.getAudioTrack());
        });
    }

    public Mono<Void> skip() {
        if(musicPlayer.getPlayingTrack() == null) return Mono.empty();
        return Mono.fromRunnable(() -> {
            musicPlayer.stopTrack();
            TrackQueueElement track = queue.dequeue();
            if (track != null)
                musicPlayer.playTrack(track.getAudioTrack());
        });
    }

    //TODO right now there are many different methods to interact with the player,
    // the best way to solve this is probably to use a command pattern with
    // a command queue and a scheduler that executes the commands
    private void leaveChannel() {

    }
}
