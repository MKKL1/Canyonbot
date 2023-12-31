package com.mkkl.canyonbot.discord.utils.pagination;

import com.mkkl.canyonbot.discord.interaction.CustomButton;
import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.music.messages.generators.QueueMessage;
import com.mkkl.canyonbot.music.messages.generators.ResponseMessageData;
import com.mkkl.canyonbot.music.messages.generators.ShortPlaylistMessage;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.Possible;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Iterator;
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

    default Mono<Response> asResponse() {
        Response.Builder builder = Response.builder();
        builder.addEmbed(pageConstructor().apply(new PageData(currentPage(), pages(), sizePerPage())));
        //builder.addComponent(ActionRow.of(CustomButton.builder().id("a").label("AA").build().asDiscordButton()));
        CustomButton nextButton = CustomButton.builder().interaction(event -> event.editReply("Next")).build();
        CustomButton prevButton = CustomButton.builder().interaction(event -> event.editReply("Prev")).build();
        builder.addComponent(ActionRow.of(nextButton.asMessageComponent(), prevButton.asMessageComponent()));
        //builder.components(Possible.of(Collections.singletonList(ActionRow.of(CustomButton.builder()))))
        return Mono.just(builder.build());
    }
}
