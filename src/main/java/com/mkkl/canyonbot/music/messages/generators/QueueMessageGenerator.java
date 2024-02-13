package com.mkkl.canyonbot.music.messages.generators;

import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.utils.pagination.Pagination;
import com.mkkl.canyonbot.discord.utils.pagination.PaginationGenerator;
import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

@Value.Immutable
public interface QueueMessageGenerator extends ResponseMessage {
    Optional<Iterator<TrackQueueElement>> queueIterator();
    Optional<TrackQueueElement> currentTrack();
    @Value.Default
    default long page() {
        return 1;
    }
    @Value.Default
    default int elementsPerPage() {
        return 10;
    }
    long maxPages();
    User caller();
    Mono<GatewayDiscordClient> gateway();

    @Override
    default Response getMessage() {
        return Pagination.builder()
                .pageConstructor(pageData -> EmbedCreateSpec.builder().description("sus").title("amogus").build())
                .build()
                .asResponse(gateway());
    }
}
