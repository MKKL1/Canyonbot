package com.mkkl.canyonbot.music.messages.generators;

import org.immutables.value.Value;
import reactor.core.publisher.Mono;

@Value.Immutable
public interface ResponseMessageDataMonoGenerator extends ResponseMessageDataGenerator {
    @Value.Default
    default Mono<Void> publisher() {
        return Mono.empty();
    }
}
