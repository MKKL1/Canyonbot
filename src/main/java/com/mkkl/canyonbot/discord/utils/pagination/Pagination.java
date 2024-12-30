package com.mkkl.canyonbot.discord.utils.pagination;

import com.mkkl.canyonbot.discord.interaction.InteractableButton;
import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.response.ResponseInteraction;
import com.mkkl.canyonbot.discord.utils.TimeoutUtils;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.component.ActionRow;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Builder;
import lombok.Value;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

@Value
@Builder
public class Pagination<T extends EmbedCreateSpec> {
    Function<PageData, T> pageConstructor;

    /**
     * Count of pages
     */
    long pages;
    @Builder.Default
    long sizePerPage = 10;

    /**
     * Current page in range [0,pages)
     */
    @Builder.Default
    long currentPage = 0;

    public Response asResponse(GatewayDiscordClient gateway) {
        Response.ResponseBuilder builder = Response.builder();
        PaginationController<T> paginationController = new PaginationController<>(currentPage, sizePerPage, pages, pageConstructor);
        builder.embeds(List.of(pageConstructor.apply(
                PageData.builder()
                    .page(currentPage)
                    .size(pages)
                    .perPage(sizePerPage)
                    .build())));

        InteractableButton nextButton = InteractableButton.builder()
                .label(">")
                .handler(event -> event
                        .deferReply()
                        .then(event.getInteractionResponse().deleteInitialResponse())
                        .then(event.getMessage().orElseThrow().edit().withEmbeds(paginationController.next()))
                )
                .build();

        InteractableButton prevButton = InteractableButton.builder()
                .label("<")
                .handler(event -> event
                        .deferReply()
                        .then(event.getInteractionResponse().deleteInitialResponse())
                        .then(event.getMessage().orElseThrow().edit().withEmbeds(paginationController.prev()))
                )
                .build();

        builder.components(List.of(ActionRow.of(prevButton.asMessageComponent(), nextButton.asMessageComponent())));
        builder.interaction(ResponseInteraction.builder()
                .interactableComponents(List.of(nextButton, prevButton))
                .gateway(gateway)
                .timeout(Duration.ofSeconds(60))
                .onTimeout(TimeoutUtils::clearActionBar)
                .build());
        return builder.build();
    }
}
