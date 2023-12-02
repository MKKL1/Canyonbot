package com.mkkl.canyonbot.discord.interaction;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

//TODO this way of handling actions is too complicated, use spring
@Configurable
@Getter
public class TempListenableButton {
    private final Button button;
    private final String id;
    @Setter
    private Function<ButtonInteractionEvent, ? extends Publisher<?>> buttonClickAction = ignore -> Mono.empty();
    @Setter
    private Function<TimeoutException, ? extends Publisher<?>> timeoutAction = ignore -> Mono.empty();
    private TempListenableButton(Button button) {
        this.button = button;
        if (button.getCustomId().isEmpty())
            throw new IllegalArgumentException("Button must have custom id");
        this.id = button.getCustomId().get();
    }

    public static TempListenableButton of(Button button) {
        return new TempListenableButton(button);
    }

    public static TempListenableButtonBuilder builder(Button button) {
        return new TempListenableButtonBuilder(button);
    }

    //TODO when button is disabled there is no reason to receive events from it

    public static class TempListenableButtonBuilder {
        private Function<ButtonInteractionEvent, ? extends Publisher<?>> buttonClickAction = ignore -> Mono.empty();
        private Function<TimeoutException, ? extends Publisher<?>> timeoutAction = ignore -> Mono.empty();
        private final Button button;

        public TempListenableButtonBuilder(Button button) {
            this.button = button;
        }

        public TempListenableButtonBuilder setButtonClickAction(Function<ButtonInteractionEvent, ? extends Publisher<?>> buttonClickAction) {
            this.buttonClickAction = buttonClickAction;
            return this;
        }
        public TempListenableButtonBuilder setTimeoutAction(Function<TimeoutException, ? extends Publisher<?>> timeoutAction) {
            this.timeoutAction = timeoutAction;
            return this;
        }
        public TempListenableButton build() {
            TempListenableButton tempListenableButton = TempListenableButton.of(button);
            tempListenableButton.setButtonClickAction(buttonClickAction);
            tempListenableButton.setTimeoutAction(timeoutAction);
            return tempListenableButton;
        }
    }
}
