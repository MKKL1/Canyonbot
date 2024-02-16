package com.mkkl.canyonbot.music.messages.generators;

import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.utils.pagination.PageData;
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
        if(queueIterator().isEmpty()) return Response.builder().content("Empty").build();



        return Pagination.builder()
                .pageConstructor(pageData -> {
                    long i = 0;
                    long startElement = pageData.getPerPage() * (pageData.getPage() - 1);
                    long endElement = pageData.getPerPage() * (pageData.getPage());
                    StringBuilder stringBuilder = new StringBuilder();

                    if (currentTrack().isPresent())
                        stringBuilder.append("Current: ")
                                .append(currentTrack().get()
                                        .getAudioTrack()
                                        .getInfo().title)
                                .append("\n");

                    stringBuilder.append("Page ")
                            .append(pageData.getPage())
                            .append("/")
                            .append(pageData.getSize())
                            .append("\n");

                    for (Iterator<TrackQueueElement> it = queueIterator().get(); it.hasNext(); i++) {

                        TrackQueueElement trackQueueElement = it.next();
                        if (trackQueueElement == null)
                            break;

                        if (i < startElement) continue;
                        if (i >= endElement) break;

                        stringBuilder.append(i + 1)
                                .append(". ")
                                .append(trackQueueElement.getAudioTrack()
                                        .getInfo().title)
                                .append("\n");

                    }

                    return EmbedCreateSpec.builder().description(stringBuilder.toString()).build();
                })
                .pages(maxPages())
                .currentPage(page())
                .sizePerPage(elementsPerPage())
                .build()
                .asResponse(gateway());
    }
}
