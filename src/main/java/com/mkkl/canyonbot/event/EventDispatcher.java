package com.mkkl.canyonbot.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;


//TODO use discord4j event dispatcher?
@Slf4j
@Component
public class EventDispatcher {
    private final int bufferSize = 10;
    //https://stackoverflow.com/questions/65186439/how-to-correctly-emit-values-to-sink-from-multiple-fluxes-websocketsessionrec
    private final Sinks.EmitFailureHandler emitFailureHandler = (signalType, emitResult) -> emitResult.equals(Sinks.EmitResult.FAIL_NON_SERIALIZED);
    private final Sinks.Many<AbstractEvent> events = Sinks.many().multicast().onBackpressureBuffer(bufferSize, false);

    public void publish(AbstractEvent event) {
        log.debug("Publishing event " + event);
        events.emitNext(event, emitFailureHandler);
    }

    public <E extends AbstractEvent> Flux<E> on(Class<E> clazz) {
        return events.asFlux().publishOn(Schedulers.boundedElastic()).ofType(clazz);
    }
}
