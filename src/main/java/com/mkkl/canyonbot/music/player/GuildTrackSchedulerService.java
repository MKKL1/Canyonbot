package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.queue.SimpleTrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.player.queue.TrackSchedulerData;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GuildTrackSchedulerService {
    //typedef would be nice here
    private final Map<Guild, TrackSchedulerData<TrackQueueElement>> trackSchedulerDataMap = new ConcurrentHashMap<>();
    private final MusicPlayerBaseService musicPlayerBaseService;

    public GuildTrackSchedulerService(MusicPlayerBaseService musicPlayerBaseService) {
        this.musicPlayerBaseService = musicPlayerBaseService;
    }

    public Mono<Void> startPlaying(Guild guild) { //Maybe we should use something like GuildSession to easily manage cleanup
        TrackSchedulerData<TrackQueueElement> trackSchedulerData = getTrackSchedulerData(guild);
        if (trackSchedulerData.getState() != TrackSchedulerData.State.STOPPED)
            return Mono.error(new IllegalStateException("Already playing"));
        return Mono.justOrEmpty(trackSchedulerData.getQueue().dequeue())
                .switchIfEmpty(Mono.error(new IllegalStateException("Queue is empty")))
                .flatMap(trackQueueElement -> {
                    //TODO not sure if using setters with object is a good idea, it means this object is mutable, therefore it cannot be safely passed around
                    trackSchedulerData.setCurrentTrack(trackQueueElement);
                    musicPlayerBaseService.playTrack(trackQueueElement.getAudioTrack());
                    trackSchedulerData.setState(TrackSchedulerData.State.PLAYING);
                    return Mono.empty();
                });
    }

    public Mono<Void> stopPlaying(Guild guild) {
        TrackSchedulerData<TrackQueueElement> trackSchedulerData = getTrackSchedulerData(guild);
        if (trackSchedulerData.getState() == TrackSchedulerData.State.STOPPED)
            return Mono.error(new IllegalStateException("Already stopped"));
        return Mono.fromRunnable(() -> {
            trackSchedulerData.getQueue().clear();
            musicPlayerBaseService.stopTrack();
            //TODO I don't like this, but I am not sure how to represent it differently.
            // Maybe TrackQueueElement could have factory method that creates object representing empty track, like TrackQueueElement.empty()
            //TODO The longer I look at this, the more I think that TrackQueue should be non-generic
            trackSchedulerData.setCurrentTrack(null);
            trackSchedulerData.setState(TrackSchedulerData.State.STOPPED);
            //TODO stop event
        });
    }

    public Mono<Void> playInstant(Guild guild, TrackQueueElement trackQueueElement) {
        return Mono.empty();
    }

    public Mono<Void> replaceCurrent(Guild guild, TrackQueueElement trackQueueElement) {
        return Mono.empty();
    }

    public Mono<TrackQueueElement> skip(Guild guild) {
        TrackSchedulerData<TrackQueueElement> trackSchedulerData = getTrackSchedulerData(guild);
        if (trackSchedulerData.getState() == TrackSchedulerData.State.STOPPED || trackSchedulerData.getCurrentTrack() == null) //Another null check
            return Mono.error(new IllegalStateException("Nothing to skip"));
        //TODO skip event
        return Mono.just(Objects.requireNonNull(trackSchedulerData.getCurrentTrack()))
                .then(Mono.fromRunnable(musicPlayerBaseService::stopTrack));
    }

    private TrackSchedulerData<TrackQueueElement> getTrackSchedulerData(Guild guild) {
        return trackSchedulerDataMap.computeIfAbsent(guild, guild1 -> new TrackSchedulerData<>(new SimpleTrackQueue()));
    }
}
