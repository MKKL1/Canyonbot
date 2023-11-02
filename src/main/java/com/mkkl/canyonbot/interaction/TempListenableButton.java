package com.mkkl.canyonbot.interaction;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.Button;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

@Getter
public abstract class TempListenableButton {
    private final Button button;
    private final String id;

    protected TempListenableButton(Button button) {
        this.button = button;
        if (button.getCustomId().isEmpty())
            throw new IllegalArgumentException("Button must have custom id");
        this.id = button.getCustomId().get();
    }

    public abstract Mono<Void> onButtonPress(ButtonInteractionEvent event);

    //TODO when button is disabled there is no reason to receive events from it
    public Mono<Void> register(DiscordClient client, Duration duration) {
        return client.withGateway(gateway -> gateway.on(ButtonInteractionEvent.class, event -> {
                    if (event.getCustomId().equals(id)) {
                        if(button.isDisabled()) return Mono.empty();
                        return onButtonPress(event);
                    }
                    return Mono.empty();
                }))
                .timeout(duration)
                .onErrorResume(TimeoutException.class, ignore -> Mono.empty())
                .then();
    }
}
