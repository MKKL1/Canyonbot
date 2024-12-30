package com.mkkl.canyonbot.discord.interaction;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.MessageComponent;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.discordjson.json.ImmutableComponentData;
import discord4j.discordjson.json.ComponentData;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Data
@Builder
public class InteractableButton implements InteractableComponent<ButtonInteractionEvent> {
    @Builder.Default
    String id = UUID.randomUUID().toString();
    String label;
    ReactionEmoji emoji;
    @Builder.Default
    Button.Style style = Button.Style.PRIMARY;
    @NonNull
    Function<ButtonInteractionEvent, Mono<?>> handler;

    @Override
    public Mono<?> getInteraction(ButtonInteractionEvent event) {
        return handler.apply(event);
    }

    public ActionComponent asMessageComponent() {
        ImmutableComponentData.Builder builder = ComponentData.builder()
                .type(MessageComponent.Type.BUTTON.getValue())
                .style(style.getValue())
                .label(label);

        if(emoji != null)
            builder.emoji(emoji.asEmojiData());

        if (style == Button.Style.LINK) {
            builder.url(getId());
        } else {
            builder.customId(getId());
        }

        // Discord4j button doesn't have a constructor with all fields (it only provides factory methods) so we have to use reflection.
        return (ActionComponent) ActionComponent.fromData(builder.build());
    }
}
