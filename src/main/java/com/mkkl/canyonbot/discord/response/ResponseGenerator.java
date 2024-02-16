package com.mkkl.canyonbot.discord.response;

import com.mkkl.canyonbot.discord.interaction.CustomButton;
import com.mkkl.canyonbot.discord.interaction.InteractableComponent;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.core.spec.MessageCreateFields;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.AllowedMentions;
import org.immutables.value.Value;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.mkkl.canyonbot.discord.PossibleUtil.toPossible;

@Value.Immutable
public interface ResponseGenerator {
    Optional<String> content();
    Optional<Boolean> tts();
    Optional<Boolean> ephemeral();
    @Value.Default
    default List<EmbedCreateSpec> embeds() {
        return Collections.emptyList();
    }
    Optional<AllowedMentions> allowedMentions();
    @Value.Default
    default List<LayoutComponent> components() {
        return Collections.emptyList();
    }
    Optional<ResponseInteraction> interaction();//TODO chaining those requires interaction().get().interaction(), it isn't very clear what's happening
    default Optional<ResponseInteraction> getResponseInteraction() {
        return interaction();
    }
    default InteractionApplicationCommandCallbackSpec asCallbackSpec() {
        return InteractionApplicationCommandCallbackSpec.builder()
                .content(toPossible(content()))
                .tts(toPossible(tts()))
                .ephemeral(toPossible(ephemeral()))
                .embeds(embeds())
                .allowedMentions(toPossible(allowedMentions()))
                .components(components())
                .build();
    }

    default InteractionFollowupCreateSpec asFollowupSpec() {
        InteractionFollowupCreateSpec.Builder builder = InteractionFollowupCreateSpec.builder();
        builder.content(toPossible(content()))
                .ephemeral(toPossible(ephemeral()))
                .allowedMentions(toPossible(allowedMentions()))
                .components(components());
        if(tts().isPresent())
            builder.tts(tts().get());

        builder.embeds(embeds());
        return builder.build();
    }
}
