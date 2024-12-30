package com.mkkl.canyonbot.discord.response;

import discord4j.core.object.component.LayoutComponent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.AllowedMentions;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;

import static com.mkkl.canyonbot.discord.PossibleUtil.toPossible;

@Value
@Builder
public class Response {
    String content;
    Boolean tts;
    Boolean ephemeral;
    @Builder.Default
    List<EmbedCreateSpec> embeds = Collections.emptyList();
    AllowedMentions allowedMentions;

    @Builder.Default
    List<LayoutComponent> components = Collections.emptyList();
    ResponseInteraction interaction;

    public InteractionApplicationCommandCallbackSpec asCallbackSpec() {
        return InteractionApplicationCommandCallbackSpec.builder()
                .content(toPossible(content))
                .tts(toPossible(tts))
                .ephemeral(toPossible(ephemeral))
                .embeds(embeds)
                .allowedMentions(toPossible(allowedMentions))
                .components(toPossible(components))
                .build();
    }

    public InteractionFollowupCreateSpec asFollowupSpec() {
        InteractionFollowupCreateSpec.Builder builder =
                InteractionFollowupCreateSpec.builder();

        builder.content(toPossible(content))
                .ephemeral(toPossible(ephemeral))
                .allowedMentions(toPossible(allowedMentions))
                .components(toPossible(components));

        if (tts != null) {
            builder.tts(tts);
        }
        builder.embeds(embeds);
        return builder.build();
    }
}
