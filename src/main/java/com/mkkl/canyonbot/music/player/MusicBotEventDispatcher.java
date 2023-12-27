package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class MusicBotEventDispatcher {
    private static final Scheduler scheduler = Schedulers.boundedElastic();
    private final Sinks.Many<MusicPlayerEvent> events;

    private MusicBotEventDispatcher(Sinks.Many<MusicPlayerEvent> events) {
        this.events = events;
    }

    public static MusicBotEventDispatcher create() {
        return new MusicBotEventDispatcher(Sinks.many().multicast().onBackpressureBuffer());
    }

    public static MusicBotEventDispatcher create(Sinks.Many<MusicPlayerEvent> events) {
        return new MusicBotEventDispatcher(events);
    }

    public void publish(MusicPlayerEvent event) {
        log.info("Publishing event " + event);
        events.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public <E extends MusicPlayerEvent> Flux<E> on(Class<E> clazz) {
        return events.asFlux().publishOn(scheduler).ofType(clazz);
    }
}
