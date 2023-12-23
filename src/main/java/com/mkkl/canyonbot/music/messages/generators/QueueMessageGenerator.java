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
public interface QueueMessageGenerator<T extends TrackQueueElement> extends ResponseMessage {
    TrackQueue<T> queue();
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
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<T> iterator = queue().iterator();
        for (int i = 0; i < page() * elementsPerPage(); i++) {
            if (!iterator.hasNext()) break;
            iterator.next();
        }
        for (int i = page() * elementsPerPage(); i < (page() + 1) * elementsPerPage(); i++) {
            if (i >= queue().size()) break;
            stringBuilder.append(i + 1)
                    .append(". ")
                    .append(iterator.next()
                            .getAudioTrack()
                            .getInfo().title)
                    .append("\n");
        }
        embedBuilder.description(stringBuilder.toString());
        embedBuilder.timestamp(Instant.now());
        embedBuilder.footer(caller().getUsername(), caller().getAvatarUrl());
        return ResponseMessageData.builder().addEmbed(embedBuilder.build()).build();
    }
}
