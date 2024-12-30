package com.mkkl.canyonbot.discord.response;

import com.mkkl.canyonbot.discord.interaction.InteractableComponent;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.entity.Message;
import lombok.Value;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import lombok.Builder;
import lombok.NonNull;

@Value
@Builder
public class ResponseInteraction {
    @Builder.Default
    List<InteractableComponent<? extends ComponentInteractionEvent>> interactableComponents = Collections.emptyList();

    @NonNull
    GatewayDiscordClient gateway; // Non-nullable field

    Duration timeout; // Nullable field for an optional timeout

    @Builder.Default
    Function<Message, Publisher<?>> onTimeout = message -> Mono.empty(); // Default timeout handler

    //TODO this should be data only class. Move this logic to service
    //TODO track current responses with interactions
    //Sometimes it's better to not pass message TODO remove message argument?
    public Mono<Void> interaction(@NonNull Message message) {
        Flux<?> flux = gateway.on(ComponentInteractionEvent.class, e -> Flux.fromIterable(interactableComponents)
                .filter(component -> e.getCustomId().equals(component.getId()))
                .flatMap(component -> ((InteractableComponent<ComponentInteractionEvent>) component)
                        .getInteraction(e)));

        if (timeout != null) { // Check if timeout is provided
            return flux
                    .timeout(timeout)
                    .onErrorResume(TimeoutException.class, ignore -> handleTimeout(onTimeout, message))
                    .then();
        }
        return flux.then();
    }

    // TODO !!unsafe cast!! This is only temporary fix as I cannot figure out correct generic to use in this situation
    private static <T> Publisher<T> handleTimeout(@NonNull Function<Message, Publisher<?>> timeoutHandler, @NonNull Message message) {
        return (Publisher<T>) timeoutHandler.apply(message);
    }
}
