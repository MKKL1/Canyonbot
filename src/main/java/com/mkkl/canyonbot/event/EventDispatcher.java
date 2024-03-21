package com.mkkl.canyonbot.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class EventDispatcher {
    private final Sinks.Many<AbstractEvent> events = Sinks.many().multicast().onBackpressureBuffer();

    public void publish(AbstractEvent event) {
        log.info("Publishing event " + event);
        events.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public <E extends AbstractEvent> Flux<E> on(Class<E> clazz) {
        return events.asFlux().publishOn(Schedulers.boundedElastic()).ofType(clazz);
    }
}
