package com.mkkl.canyonbot.discord.interaction;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.immutables.value.Value;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

//TODO in this case use Immutables.Value to be consistent with messages
public class TempListenableButton {
    @Getter
    private final Button button;
    @Getter
    private final String id;
    private Function<ButtonInteractionEvent, ? extends Publisher<?>> buttonClickAction = ignore -> Mono.empty();
    private Supplier<? extends Mono<Void>> timeoutAction = Mono::empty;

    public TempListenableButton(Button button,
                                String id,
                                Function<ButtonInteractionEvent, ? extends Publisher<?>> buttonClickAction,
                                Supplier<? extends Mono<Void>> timeoutAction) {
        this.button = button;
        this.id = id;
        this.buttonClickAction = buttonClickAction;
        this.timeoutAction = timeoutAction;
    }

    public Mono<Void> register(DiscordClient client, Duration duration) {
        return client.withGateway(gateway -> gateway.on(ButtonInteractionEvent.class)
                        .filter(event -> event.getCustomId().equals(id))
                        .flatMap(event -> buttonClickAction.apply(event))
                )
                .timeout(duration)
                .onErrorResume(TimeoutException.class, ignore -> timeoutAction.get());
    }

    public static Builder builder(Button button) {
        return new Builder(button);
    }

    public static class Builder {
        private final Button button;
        private final String id;

        public Builder(Button button) {
            this.button = button;
            this.id = button.getCustomId()
                    .orElseThrow(() -> new IllegalArgumentException("Button has to have custom id"));
        }

        private Function<ButtonInteractionEvent, ? extends Publisher<?>> buttonClickAction = ignore -> Mono.empty();
        private Supplier<? extends Mono<Void>> timeoutAction = Mono::empty;

        public Builder clickAction(Function<ButtonInteractionEvent, ? extends Publisher<?>> action) {
            buttonClickAction = action;
            return this;
        }

        public Builder timeoutAction(Supplier<? extends Mono<Void>> action) {
            timeoutAction = action;
            return this;
        }

        public TempListenableButton build() {
            return new TempListenableButton(button, id, buttonClickAction, timeoutAction);
        }

    }
}
