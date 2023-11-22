package com.mkkl.canyonbot.music.player.queue;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.MusicBotEventDispatcher;
import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import com.mkkl.canyonbot.music.player.event.base.TrackEndEvent;
import com.mkkl.canyonbot.music.player.event.scheduler.QueueEmptyEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import reactor.core.publisher.Mono;

public class TrackScheduler {
    private final TrackQueue<TrackQueueElement> queue;
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
                            if (track != null)
                                musicPlayerBase.playTrack(track.getAudioTrack());
                            else {
                                musicBotEventDispatcher.publish(new QueueEmptyEvent(guildMusicBotManager, queue));
                                state = State.STOPPED;
                            }
                        });
                    }
                    //Cleanup
                    return guildMusicBotManager.leave();//TODO event here as well
                })
                .subscribe();
        this.musicPlayerBase = musicPlayerBase;
    }

    public Mono<Void> start() {
        if (state != State.STOPPED) return Mono.empty();//TODO return error
        return Mono.fromRunnable(() -> {
            TrackQueueElement track = queue.dequeue();
            if (track != null) {
                musicPlayerBase.playTrack(track.getAudioTrack());
                state = State.PLAYING;
                //TODO start event
            }
            //TODO return error
        });
    }

    public Mono<Void> stop() {
        if (state == State.STOPPED) return Mono.empty();
        return Mono.fromRunnable(() -> {
            queue.clear();
            musicPlayerBase.stopTrack();
            state = State.STOPPED;
            //TODO stop event
        });
    }

    public Mono<Void> skip() {//TODO return skipped track
        if (state == State.STOPPED) return Mono.empty();
        //TODO skip event
        return Mono.fromRunnable(musicPlayerBase::stopTrack);
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
