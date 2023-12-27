package com.mkkl.canyonbot.music.messages.generators;

import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.*;

@Value.Immutable
public interface QueueMessageGenerator extends ResponseMessage {
    Optional<Iterator<TrackQueueElement>> queueIterator();
    Optional<TrackQueueElement> currentTrack();
    @Value.Default
    default int page() {
        return 0;
    }
    @Value.Default
    default int elementsPerPage() {
        return 10;
    }
    User caller();

    //TODO this part of code requires massive refactoring
    @Override
    default ResponseMessageData getMessage() {
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
        embedBuilder.title("Queue");
        embedBuilder.timestamp(Instant.now());
        embedBuilder.footer(caller().getUsername(), caller().getAvatarUrl());
        if (queueIterator().isEmpty()) {
            embedBuilder.description("Queue is empty");
            return ResponseMessageData.builder().addEmbed(embedBuilder.build()).build();
        }

        int i = 0;
        int startElement = elementsPerPage()*page();
        StringBuilder stringBuilder = new StringBuilder();
        if(currentTrack().isPresent())
            stringBuilder.append("Current: ").append(currentTrack().get().getAudioTrack().getInfo().title).append("\n");

        for (Iterator<TrackQueueElement> it = queueIterator().get(); it.hasNext(); ) {
            TrackQueueElement trackQueueElement = it.next();
            if(trackQueueElement == null)
                break;
            if(i < startElement) continue;

            stringBuilder.append(i + 1)
                    .append(". ")
                    .append(trackQueueElement.getAudioTrack().getInfo().title)
                    .append("\n");
            if(i >= elementsPerPage()) break;
            i++;
        }
        embedBuilder.description(stringBuilder.toString());
        return ResponseMessageData.builder().addEmbed(embedBuilder.build()).build();
    }
}
