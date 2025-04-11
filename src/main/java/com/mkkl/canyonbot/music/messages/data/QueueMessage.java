package com.mkkl.canyonbot.music.messages.data;

import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.utils.pagination.Pagination;
import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Iterator;

@Value
@Builder
public class QueueMessage implements ResponseMessage {
    Iterator<TrackQueueElement> queueIterator;
    TrackQueueElement currentTrack;
    @Builder.Default
    long page = 1;
    @Builder.Default
    int elementsPerPage = 10;
    long maxPages;
    @NonNull
    User caller;
    @NonNull
    GatewayDiscordClient gateway;

    @Override
    public Response getMessage() {
        if (queueIterator == null) return Response.builder().content("Empty").build();

        return Pagination.builder()
                .pageConstructor(pageData -> {
                    long i = 0;
                    long startElement = pageData.getPerPage() * (pageData.getPage() - 1);
                    long endElement = pageData.getPerPage() * (pageData.getPage());
                    StringBuilder stringBuilder = new StringBuilder();

                    if (currentTrack != null)
                        stringBuilder.append("Current: ")
                                .append(currentTrack
                                        .getTrack()
                                        .getInfo().getTitle())
                                .append("\n");

                    stringBuilder.append("Page ")
                            .append(pageData.getPage())
                            .append("/")
                            .append(pageData.getSize())
                            .append("\n");

                    for (Iterator<TrackQueueElement> it = queueIterator; it.hasNext(); i++) {

                        TrackQueueElement trackQueueElement = it.next();
                        if (trackQueueElement == null)
                            break;

                        if (i < startElement) continue;
                        if (i >= endElement) break;

                        stringBuilder.append(i + 1)
                                .append(". ")
                                .append(trackQueueElement.getTitle())
                                .append("\n");

                    }

                    return EmbedCreateSpec.builder().description(stringBuilder.toString()).build();
                })
                .pages(maxPages)
                .currentPage(page)
                .sizePerPage(elementsPerPage)
                .build()
                .asResponse(gateway);
    }
}