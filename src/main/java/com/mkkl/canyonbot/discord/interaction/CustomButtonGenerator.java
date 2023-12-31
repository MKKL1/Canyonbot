package com.mkkl.canyonbot.discord.interaction;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.MessageComponent;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.discordjson.json.ComponentData;
import discord4j.discordjson.json.EmojiData;
import discord4j.discordjson.json.ImmutableComponentData;
import discord4j.discordjson.possible.Possible;
import org.immutables.value.Value;

import java.util.Optional;

import static com.mkkl.canyonbot.discord.PossibleUtil.mapPossible;
import static com.mkkl.canyonbot.discord.PossibleUtil.toPossible;

@Value.Style(
        typeAbstract = "*Generator",
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        deepImmutablesDetection = true,
        allMandatoryParameters = true,
        depluralize = true,
        instance = "create"
)
@Value.Immutable
public interface CustomButtonGenerator extends InteractableComponent<ButtonInteractionEvent> {
    Optional<String> id();
    Optional<String> label();
    Optional<ReactionEmoji> emoji();
    @Value.Default
    default Button.Style style() {
        return Button.Style.PRIMARY;
    }

    default ActionComponent asMessageComponent() {
        ImmutableComponentData.Builder builder = ComponentData.builder()
                .type(MessageComponent.Type.BUTTON.getValue())
                .style(style().getValue())
                .label(toPossible(label()));
        if(emoji().isPresent())
            builder.emoji(emoji().get().asEmojiData());
        if(style() == Button.Style.LINK)
            builder.url(toPossible(id()));
        else builder.customId(toPossible(id()));

        //Discord4j button doesn't have a constructor with all fields (it only provides factory methods) so we have to use reflection
        return (ActionComponent) ActionComponent.fromData(builder.build());
    }
}
