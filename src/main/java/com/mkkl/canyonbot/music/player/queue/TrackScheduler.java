package com.mkkl.canyonbot.music.player.queue;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.MusicBotEventDispatcher;
import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import com.mkkl.canyonbot.music.player.event.base.TrackEndEvent;
import com.mkkl.canyonbot.music.player.event.scheduler.QueueEmptyEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import jakarta.annotation.Nullable;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class TrackScheduler {
    private final TrackQueue<TrackQueueElement> queue;
    @Nullable
    @Getter
    private volatile TrackQueueElement currentTrack;
    private final MusicPlayerBase musicPlayerBase;
    @Getter
    private State state = State.STOPPED;

    //TODO right now if TrackScheduler::stop is called, TrackEndEvent, QueueEmptyEvent and SchedulerStopEvent are published, which may lead to misunderstandings

    //Use create
    //Maybe even builder
    public TrackScheduler(TrackQueue<TrackQueueElement> queue,
                          MusicPlayerBase musicPlayerBase,
                          MusicBotEventDispatcher musicBotEventDispatcher,
                          GuildMusicBotManager guildMusicBotManager) {
        this.queue = queue;
        musicBotEventDispatcher.on(TrackEndEvent.class)
                .flatMap(trackEndEvent -> {
                    if (trackEndEvent.getEndReason() != AudioTrackEndReason.CLEANUP) {
                        return Mono.fromRunnable(() -> {
                            TrackQueueElement track = queue.dequeue();
                            currentTrack = track;
                            if (track != null) {
                                musicPlayerBase.playTrack(track.getAudioTrack());
                            } else {
                                musicBotEventDispatcher.publish(new QueueEmptyEvent(guildMusicBotManager, queue));
                                state = State.STOPPED;
                            }
                        });
                    }
                    state = State.STOPPED;
                    currentTrack = null;
                    //Cleanup
                    return guildMusicBotManager.leave();//TODO event here as well
                })
                .subscribe();
        this.musicPlayerBase = musicPlayerBase;
    }

    public Mono<Void> start() {
        if (state != State.STOPPED) return Mono.error(new IllegalStateException("Already playing"));
        return Mono.justOrEmpty(queue.dequeue())
                .switchIfEmpty(Mono.error(new IllegalStateException("Queue is empty")))
                .flatMap(trackQueueElement -> {
                    currentTrack = trackQueueElement;
                    musicPlayerBase.playTrack(trackQueueElement.getAudioTrack());
                    state = State.PLAYING;
                    return Mono.empty();
                });
    }

    public Mono<Void> playInstant(TrackQueueElement trackQueueElement) {
        return Mono.empty();
    }

    public Mono<Void> replaceCurrent(TrackQueueElement trackQueueElement) {
        return Mono.empty();
    }

    public Mono<Void> stop() {
        if (state == State.STOPPED) return Mono.error(new IllegalStateException("Already stopped"));
        return Mono.fromRunnable(() -> {
            queue.clear();
            musicPlayerBase.stopTrack();
            currentTrack = null;
            state = State.STOPPED;
            //TODO stop event
        });
    }

    public Mono<TrackQueueElement> skip() {
        if (state == State.STOPPED || currentTrack == null)
            return Mono.error(new IllegalStateException("Nothing to skip"));
        //TODO skip event
        return Mono.just(Objects.requireNonNull(currentTrack))
                .then(Mono.fromRunnable(musicPlayerBase::stopTrack));
    }

    public Mono<Void> pause() {
        return Mono.empty();
    }

    public Mono<Void> unpause() {
        return Mono.empty();
    }

    public enum State {
        PLAYING,
        PAUSED,
        STOPPED
    }
}
