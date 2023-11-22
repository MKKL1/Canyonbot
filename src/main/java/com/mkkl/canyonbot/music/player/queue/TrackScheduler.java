package com.mkkl.canyonbot.music.player.queue;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.MusicBotEventDispatcher;
import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import com.mkkl.canyonbot.music.player.event.base.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import reactor.core.publisher.Mono;

public class TrackScheduler {
    private final TrackQueue<TrackQueueElement> queue;
    private final MusicPlayerBase musicPlayerBase;
    @Getter
    private State state = State.STOPPED;

    //Use create
    //Maybe even builder
    public TrackScheduler(TrackQueue<TrackQueueElement> queue, MusicPlayerBase musicPlayerBase, MusicBotEventDispatcher musicBotEventDispatcher, GuildMusicBotManager guildMusicBotManager) {
        this.queue = queue;
        musicBotEventDispatcher.on(TrackEndEvent.class)
                .flatMap(trackEndEvent -> {
                    if (trackEndEvent.getEndReason() == AudioTrackEndReason.FINISHED) {
                        return Mono.fromRunnable(() -> {
                            TrackQueueElement track = this.queue.dequeue();
                            if (track != null)
                                musicPlayerBase.playTrack(track.getAudioTrack());
                            else guildMusicBotManager.leave()
                                    .block(); //TODO this is a bit hacky, better way would be to dispatch a PlayerEmptyEvent
                        });
                    }
                    return guildMusicBotManager.leave();
                })
                .subscribe();
        this.musicPlayerBase = musicPlayerBase;
    }

    public Mono<Void> start() {
        if (musicPlayerBase.getPlayingTrack() != null) return Mono.empty();
        return Mono.fromRunnable(() -> {
            TrackQueueElement track = queue.dequeue();
            if (track != null) {
                musicPlayerBase.playTrack(track.getAudioTrack());
                state = State.PLAYING;
            }
        });
    }

    public Mono<Void> skip() {
        if (musicPlayerBase.getPlayingTrack() == null) return Mono.empty();
        return Mono.fromRunnable(() -> {
            musicPlayerBase.stopTrack();
            TrackQueueElement track = queue.dequeue();
            if (track != null)
                musicPlayerBase.playTrack(track.getAudioTrack());
        });
    }

    public enum State {
        PLAYING,
        PAUSED,
        STOPPED
    }
}
