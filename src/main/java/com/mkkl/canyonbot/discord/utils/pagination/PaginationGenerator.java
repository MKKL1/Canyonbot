package com.mkkl.canyonbot.discord.utils.pagination;

import com.mkkl.canyonbot.discord.interaction.CustomButton;
import com.mkkl.canyonbot.discord.interaction.ImmutableCustomButton;
import com.mkkl.canyonbot.discord.interaction.InteractableComponent;
import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.response.ResponseInteraction;
import com.mkkl.canyonbot.music.messages.generators.QueueMessage;
import com.mkkl.canyonbot.music.messages.generators.ResponseMessageData;
import com.mkkl.canyonbot.music.messages.generators.ShortPlaylistMessage;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.Possible;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.immutables.value.Value;
import org.springframework.beans.factory.annotation.Configurable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import static com.mkkl.canyonbot.discord.PossibleUtil.mapPossible;

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
public interface PaginationGenerator<T extends EmbedCreateSpec> {
    Function<PageData, T> pageConstructor();
    int pages();
    @Value.Default
    default int sizePerPage() {
        return 10;
    }
    @Value.Default
    default int currentPage() {
        return 0;
    }

    default Response asResponse(Mono<GatewayDiscordClient> gateway) {
        Response.Builder builder = Response.builder();
        builder.addEmbed(pageConstructor().apply(new PageData(currentPage(), pages(), sizePerPage())));
        CustomButton nextButton = ImmutableCustomButton.builder().interaction(event -> event.editReply("Next")).id("next").build();
        CustomButton prevButton = ImmutableCustomButton.builder().interaction(event -> event.editReply("Prev")).id("prev").build();
        builder.addComponent(ActionRow.of(nextButton.asMessageComponent(), prevButton.asMessageComponent()));
        builder.interaction(ResponseInteraction.builder()
                .addInteractableComponent(nextButton)
                .addInteractableComponent(prevButton)
                .gateway(gateway)
                .timeout(Duration.ofSeconds(60))
                .build());
        return builder.build();
    }
}
