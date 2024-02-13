package com.mkkl.canyonbot.discord.interaction;

import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.event.domain.interaction.InteractionCreateEvent;
import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.component.MessageComponent;
import org.reactivestreams.Publisher;

import java.util.Optional;
import java.util.function.Function;

public interface InteractableComponent<T extends ComponentInteractionEvent> {
    Function<T, Publisher<?>> getInteraction();
    Optional<String> getId();
    ActionComponent asMessageComponent();
}
