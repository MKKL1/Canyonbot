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

    //Use create
    //Maybe even builder
    public TrackScheduler(TrackQueue<TrackQueueElement> queue,
                          MusicPlayerBase musicPlayerBase,
                          MusicBotEventDispatcher musicBotEventDispatcher,
                          GuildMusicBotManager guildMusicBotManager) {
        this.queue = queue;
        musicBotEventDispatcher.on(TrackEndEvent.class)
                .flatMap(trackEndEvent -> handleTrackEndEvent(trackEndEvent, guildMusicBotManager, queue, musicPlayerBase, musicBotEventDispatcher))
                .subscribe();
        this.musicPlayerBase = musicPlayerBase;
    }

    private static Mono<Void> handleTrackEndEvent(TrackEndEvent trackEndEvent, GuildMusicBotManager guildMusicBotManager, TrackQueue<TrackQueueElement> queue, MusicPlayerBase musicPlayerBase, MusicBotEventDispatcher musicBotEventDispatcher) {
        if (trackEndEvent.getEndReason().mayStartNext) {
            return Mono.fromRunnable(() -> {
                TrackQueueElement track = queue.dequeue();
                if (track != null)
                    musicPlayerBase.playTrack(track.getAudioTrack());
                else musicBotEventDispatcher.publish(new QueueEmptyEvent(guildMusicBotManager, queue));
            });
        }
        return guildMusicBotManager.leave();//TODO event here as well
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

    public Mono<Void> pause() {
        return Mono.empty();
    }
    public Mono<Void> unpause() {
        return Mono.empty();
    }

    public Mono<Void> skip() {
        if (musicPlayerBase.getPlayingTrack() == null) return Mono.empty();//TODO return error
        return Mono.fromRunnable(() -> {
            musicPlayerBase.stopTrack();
            TrackQueueElement track = queue.dequeue();
            if (track != null)
                musicPlayerBase.playTrack(track.getAudioTrack());//TODO skip event
            //TODO return error
        });
    }

    public enum State {
        PLAYING,
        PAUSED,
        STOPPED
    }
}
