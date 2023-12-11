package com.mkkl.canyonbot.music.player.queue;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class TrackSchedulerData<T extends TrackQueueElement> {
    //TODO replace with object that holds info about currently played track like current time


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

    public enum State {
        PLAYING,
        PAUSED,
        STOPPED
    }
}
