package com.mkkl.canyonbot.discord.interaction;

import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.component.ActionComponent;
import org.reactivestreams.Publisher;

import java.util.function.Function;

public interface InteractableComponent<T extends ComponentInteractionEvent> {
    Function<T, Publisher<?>> getInteraction();
    String getId();
    ActionComponent asMessageComponent();
}
