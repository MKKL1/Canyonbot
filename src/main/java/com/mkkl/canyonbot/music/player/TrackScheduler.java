package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.event.lavalink.player.PlayerTrackEndEvent;
import com.mkkl.canyonbot.music.player.event.scheduler.QueueEmptyEvent;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import dev.arbjerg.lavalink.client.LavalinkPlayer;
import dev.arbjerg.lavalink.client.Link;
import discord4j.core.object.entity.Guild;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import reactor.core.publisher.Mono;

@Slf4j
@Getter
public class TrackScheduler {
    @Nullable
    private TrackQueueElement currentTrack = null;
    private State state = State.STOPPED;
    private final TrackQueue trackQueue;
    private final Link link;
    //It is only needed in constructor so should I just pass it as a parameter?
    public TrackScheduler(TrackQueue trackQueue, Link link, Guild guild, EventDispatcher eventDispatcher) {
        this.link = link;
        this.trackQueue = trackQueue;
        eventDispatcher.on(PlayerTrackEndEvent.class)
                .filter(trackEndEvent -> trackEndEvent.getReason().getMayStartNext())
                .flatMap(trackEndEvent ->
                        //May play next
                        playNext().switchIfEmpty(Mono.defer(() -> {
                            //Queue empty
                            eventDispatcher.publish(new QueueEmptyEvent(guild.getId().asLong(), trackQueue));
                            state = State.STOPPED;
                            return Mono.empty();
                        })))
                .switchIfEmpty(Mono.defer(() -> {
                    //Cannot play next
                    state = State.STOPPED;
                    currentTrack = null;
                    return Mono.empty();
                })).subscribe();
    }

    public Mono<Void> beginPlayback() {
        return playNext()
                .switchIfEmpty(Mono.error(new IllegalStateException("Queue is empty")))
                .then(Mono.fromRunnable(() -> state = State.PLAYING));
    }

    public Mono<Void> stop() {
        return link.getPlayer()
                .flatMap(LavalinkPlayer::stopTrack)
                .doOnNext(ignore -> {
                    trackQueue.clear();
                    currentTrack = null;
                    state = State.STOPPED;
                }).then();
        //TODO stop event
    }

    public Mono<TrackQueueElement> skip() {
        if (state == TrackScheduler.State.STOPPED || currentTrack == null)
            return Mono.error(new IllegalStateException("Nothing to skip"));
        //TODO skip event
        return Mono.justOrEmpty(currentTrack)
                .flatMap(track -> playNext().then(Mono.just(track)));
    }

    private Mono<LavalinkPlayer> playNext() {
        return Mono.fromCallable(trackQueue::dequeue)
                .doOnNext(trackQueueElement -> currentTrack = trackQueueElement)
                .flatMap(trackQueueElement -> link.getPlayer().flatMap(player -> player.setTrack(trackQueueElement.getTrack())));
    }



    public enum State {
        PLAYING,
        PAUSED,
        STOPPED
    }
}
