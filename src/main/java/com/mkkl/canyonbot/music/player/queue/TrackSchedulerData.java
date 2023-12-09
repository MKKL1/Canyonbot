package com.mkkl.canyonbot.music.player.queue;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.MusicBotEventDispatcher;
import com.mkkl.canyonbot.music.player.MusicPlayerBaseService;
import com.mkkl.canyonbot.music.player.event.base.TrackEndEvent;
import com.mkkl.canyonbot.music.player.event.scheduler.QueueEmptyEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Data
public class TrackSchedulerData<T extends TrackQueueElement> {
    //TODO replace with object that holds info about currently played track like current time
    @Nullable
    private T currentTrack;
    private final TrackQueue<T> queue;
    private State state = State.STOPPED;

    //TODO right now if TrackScheduler::stop is called, TrackEndEvent, QueueEmptyEvent and SchedulerStopEvent are published, which may lead to misunderstandings

    //Use create
    //Maybe even builder
//    public TrackSchedulerData(TrackQueue<TrackQueueElement> queue,
//                              MusicPlayerBaseService musicPlayerBaseService,
//                              MusicBotEventDispatcher musicBotEventDispatcher,
//                              GuildMusicBotManager guildMusicBotManager) {
//        this.queue = queue;
//        musicBotEventDispatcher.on(TrackEndEvent.class)
//                .flatMap(trackEndEvent -> {
//                    if (trackEndEvent.getEndReason() != AudioTrackEndReason.CLEANUP) {
//                        return Mono.fromRunnable(() -> {
//                            TrackQueueElement track = queue.dequeue();
//                            currentTrack = track;
//                            if (track != null) {
//                                musicPlayerBaseService.playTrack(track.getAudioTrack());
//                            } else {
//                                musicBotEventDispatcher.publish(new QueueEmptyEvent(guildMusicBotManager, queue));
//                                state = State.STOPPED;
//                            }
//                        });
//                    }
//                    state = State.STOPPED;
//                    currentTrack = null;
//                    //Cleanup
//                    return guildMusicBotManager.leave();//TODO event here as well
//                })
//                .subscribe();
//        this.musicPlayerBaseService = musicPlayerBaseService;
//    }





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
