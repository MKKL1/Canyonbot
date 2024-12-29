package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.event.MayDestroyPlayerEvent;
import com.mkkl.canyonbot.music.player.event.lavalink.player.PlayerTrackEndEvent;
import com.mkkl.canyonbot.music.player.event.scheduler.PlayNextEvent;
import com.mkkl.canyonbot.music.player.event.scheduler.QueueEmptyEvent;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Slf4j
public class TrackScheduler implements Disposable{
    @Getter
    private TrackQueueElement currentTrack = null;
    @Getter
    private State state = State.STOPPED;
    @Getter
    private final TrackQueue trackQueue;
    @Getter
    private final Link link;

    private final EventDispatcher eventDispatcher;

    private final Disposable playerTrackEndEventSub;
    public TrackScheduler(TrackQueue trackQueue, Link link, long guildId, EventDispatcher eventDispatcher) {
        this.link = link;
        this.trackQueue = trackQueue;
        this.eventDispatcher = eventDispatcher;

        playerTrackEndEventSub = eventDispatcher.on(PlayerTrackEndEvent.class)
                .filter(trackEndEvent -> trackEndEvent.getReason().getMayStartNext())
                .flatMap(trackEndEvent ->
                        //May play next
                        playNext().switchIfEmpty(Mono.fromRunnable(() -> {
                            //Queue empty
                            state = State.STOPPED;
                            eventDispatcher.publish(new QueueEmptyEvent(guildId, trackQueue));
                            eventDispatcher.publish(new MayDestroyPlayerEvent(guildId));
                        })))
                .switchIfEmpty(Mono.fromRunnable(() -> {
                    //Cannot play next
                    state = State.STOPPED;
                    currentTrack = null;
                    eventDispatcher.publish(new MayDestroyPlayerEvent(guildId));
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
                .flatMap(trackQueueElement -> link.createOrUpdatePlayer().setTrack(trackQueueElement.getTrack()))
                .doOnNext(lavalinkPlayer ->
                        eventDispatcher.publish(new PlayNextEvent(lavalinkPlayer.getGuildId(), trackQueue, currentTrack)));
    }

    public Mono<Void> pause() {
        return Mono.empty(); //TODO implement
    }

    @Override
    public void dispose() {
        playerTrackEndEventSub.dispose();
    }

    @Override
    public boolean isDisposed() {
        return playerTrackEndEventSub.isDisposed();
    }

    public enum State {
        PLAYING,
        PAUSED,
        STOPPED
    }
}
