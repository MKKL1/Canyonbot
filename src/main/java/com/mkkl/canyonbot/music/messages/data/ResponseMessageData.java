package com.mkkl.canyonbot.music.messages.data;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.object.component.LayoutComponent;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder
public class ResponseMessageData {
    @Builder.Default
    List<EmbedCreateSpec> embeds = Collections.emptyList();

    @Builder.Default
    List<LayoutComponent> components = Collections.emptyList();

    public EmbedCreateSpec firstEmbed() {
        return embeds.getFirst(); // Changed from `getFirst` (no method `getFirst` exists for List)
    }
}
