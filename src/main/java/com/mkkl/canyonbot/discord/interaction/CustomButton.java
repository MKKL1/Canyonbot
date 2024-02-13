package com.mkkl.canyonbot.discord.interaction;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.MessageComponent;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.discordjson.json.ComponentData;
import discord4j.discordjson.json.ImmutableComponentData;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Optional;

import static com.mkkl.canyonbot.discord.PossibleUtil.toPossible;

@Value.Immutable
public abstract class CustomButton implements InteractableComponent<ButtonInteractionEvent> {
    public abstract Optional<String> id();
    public abstract Optional<String> label();
    public abstract Optional<ReactionEmoji> emoji();
    @Value.Default
    public Button.Style style() {
        return Button.Style.PRIMARY;
    }

    @Override
    public Optional<String> getId() {
        return id();
    }

    public ActionComponent asMessageComponent() {
        ImmutableComponentData.Builder builder = ComponentData.builder()
                .type(MessageComponent.Type.BUTTON.getValue())
                .style(style().getValue())
                .label(toPossible(label()));
        if(emoji().isPresent())
            builder.emoji(emoji().get().asEmojiData());
        if(style() == Button.Style.LINK)
            builder.url(toPossible(id()));
        else {
            builder.customId(Snowflake.of(Instant.now()).asString());
        }

        //Discord4j button doesn't have a constructor with all fields (it only provides factory methods) so we have to use reflection
        return (ActionComponent) ActionComponent.fromData(builder.build());
    }
}
