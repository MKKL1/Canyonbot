package com.mkkl.canyonbot.discord.response;

import com.mkkl.canyonbot.discord.interaction.InteractableComponent;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.possible.Possible;
import org.immutables.value.Value;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

@Value.Immutable
public interface ResponseInteractionGenerator {
    @Value.Default
    default List<InteractableComponent<? extends ComponentInteractionEvent>> interactableComponents() {
        return Collections.emptyList();
    }
    Mono<GatewayDiscordClient> gateway();
    Optional<Duration> timeout();
    @Value.Default
    default Function<Message, Mono<Void>> onTimeout() {
        return message -> Mono.empty();
    }

    default Mono<Void> interaction(Message message) {
         Mono<Void> mono = gateway().flatMap(gateway ->
                gateway.on(ComponentInteractionEvent.class)
                        .doOnNext(e -> System.out.println(e.getCustomId()))
                        //iterate over all interactable components
                        .zipWith(Flux.fromIterable(interactableComponents()))
                        .filter(objects -> {
                            ComponentInteractionEvent event = objects.getT1();
                            InteractableComponent<ComponentInteractionEvent> component = (InteractableComponent<ComponentInteractionEvent>) objects.getT2();
                            return event.getCustomId()
                                    .equals(component.getId());
                        })
                        .flatMap(objects -> {
                            ComponentInteractionEvent event = objects.getT1();
                            InteractableComponent<ComponentInteractionEvent> component = (InteractableComponent<ComponentInteractionEvent>) objects.getT2();
                            return component.getInteraction()
                                    .apply(event);
                        }).then());
         if(timeout().isPresent()) {
             mono = mono
                     .timeout(timeout().get())
                     .onErrorResume(TimeoutException.class, ignore -> onTimeout().apply(message));
         }
         return mono;
    }
}
