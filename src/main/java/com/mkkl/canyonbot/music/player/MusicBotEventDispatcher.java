package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class MusicBotEventDispatcher {
    private static final Scheduler scheduler = Schedulers.boundedElastic();
    private final Flux<MusicPlayerEvent> eventFlux;

    private MusicBotEventDispatcher(Flux<MusicPlayerEvent> eventFlux) {
        this.eventFlux = eventFlux;
    }

    public static MusicBotEventDispatcher create(Flux<MusicPlayerEvent> eventFlux) {
        return new MusicBotEventDispatcher(eventFlux);
    }

    public <E extends MusicPlayerEvent> Flux<E> on(Class<E> clazz) {
        return eventFlux.publishOn(scheduler).ofType(clazz);
    }
}
