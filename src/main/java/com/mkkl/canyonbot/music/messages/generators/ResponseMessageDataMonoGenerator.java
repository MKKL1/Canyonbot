package com.mkkl.canyonbot.music.messages.generators;

import discord4j.core.object.component.LayoutComponent;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Value.Immutable
public interface ResponseMessageDataMonoGenerator extends ResponseMessageDataGenerator {
    @Value.Default
    default Mono<Void> publisher() {
        return Mono.empty();
    }
}
