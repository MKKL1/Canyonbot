package com.mkkl.canyonbot.discord.utils.pagination;

import com.mkkl.canyonbot.discord.interaction.CustomButton;
import com.mkkl.canyonbot.discord.interaction.ImmutableCustomButton;
import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.response.ResponseInteraction;
import com.mkkl.canyonbot.discord.utils.TimeoutUtils;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.component.ActionRow;
import discord4j.core.spec.EmbedCreateSpec;
import org.immutables.value.Value;

import java.time.Duration;
import java.util.function.Function;

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

    /**
     * Count of pages
     */
    long pages();
    @Value.Default
    default long sizePerPage() {
        return 10;
    }

    /**
     * Current page in range [0,pages)
     */
    @Value.Default
    default long currentPage() {
        return 0;
    }

    default Response asResponse(GatewayDiscordClient gateway) {
        Response.Builder builder = Response.builder();
        PaginationController<T> paginationController = new PaginationController<>(currentPage(), sizePerPage(), pages(), pageConstructor());
        builder.addEmbed(pageConstructor().apply(new PageData(currentPage(), pages(), sizePerPage())));
        CustomButton nextButton = ImmutableCustomButton.builder()
                .label(">")
                .interaction(event -> event
                        .deferReply()
                        .then(event.getInteractionResponse().deleteInitialResponse())
                        .then(event.getMessage().orElseThrow().edit().withEmbeds(paginationController.next()))
                ).build();
        CustomButton prevButton = ImmutableCustomButton.builder()
                .label("<")
                .interaction(event -> event
                        .deferReply()
                        .then(event.getInteractionResponse().deleteInitialResponse())
                        .then(event.getMessage().orElseThrow().edit().withEmbeds(paginationController.prev())))
                .build();
        builder.addComponent(ActionRow.of(prevButton.asMessageComponent(), nextButton.asMessageComponent()));
        builder.interaction(ResponseInteraction.builder()
                .addInteractableComponent(nextButton)
                .addInteractableComponent(prevButton)
                .gateway(gateway)
                .timeout(Duration.ofSeconds(60))
                .onTimeout(TimeoutUtils::clearActionBar)
                .build());
        return builder.build();
    }
}
