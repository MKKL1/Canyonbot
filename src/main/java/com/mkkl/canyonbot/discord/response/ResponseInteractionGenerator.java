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
    GatewayDiscordClient gateway();
    Optional<Duration> timeout();
    @Value.Default
    default Function<Message, Publisher<?>> onTimeout() {
        return message -> Mono.empty();
    }

    //TODO track current responses with interactions
    //Sometimes it's better to not pass message TODO remove message argument?
    default Mono<Void> interaction(Message message) {
         Flux<?> flux = gateway().on(ComponentInteractionEvent.class, e -> {
             return Flux.fromIterable(interactableComponents())
                     .filter(component -> e.getCustomId()
                             .equals(component.getId()))
                     .flatMap(component -> ((InteractableComponent<ComponentInteractionEvent>) component).getInteraction()
                             .apply(e));
         });

         if(timeout().isPresent()) {
             return flux
                     .timeout(timeout().get())
                     .onErrorResume(TimeoutException.class, ignore -> handleTimeout(onTimeout(),message))
                     .then();
         }
         return flux.then();
    }

    //TODO !!unsafe cast!! This is only temporary fix as I cannot figure out correct generic to use in this situation
    private static <T> Publisher<T> handleTimeout(Function<Message, Publisher<?>> timeoutHandler, Message message) {
        return (Publisher<T>) timeoutHandler.apply(message);
    }
}
