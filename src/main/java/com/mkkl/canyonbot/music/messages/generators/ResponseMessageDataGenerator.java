package com.mkkl.canyonbot.music.messages.generators;

import discord4j.core.spec.EmbedCreateSpec;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface ResponseMessageDataGenerator {
    List<EmbedCreateSpec> embeds();

    default EmbedCreateSpec first() {
        return embeds().getFirst();
    }
}
