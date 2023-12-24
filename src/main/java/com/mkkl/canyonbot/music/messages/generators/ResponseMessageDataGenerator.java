package com.mkkl.canyonbot.music.messages.generators;

import discord4j.core.object.component.LayoutComponent;
import discord4j.core.spec.EmbedCreateSpec;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
public interface ResponseMessageDataGenerator {
    @Value.Default
    default List<EmbedCreateSpec> embeds() {
        return Collections.emptyList();
    }
    @Value.Default
    default List<LayoutComponent> components() {
        return Collections.emptyList();
    }

    default EmbedCreateSpec firstEmbed() {
        return embeds().getFirst();
    }
}
