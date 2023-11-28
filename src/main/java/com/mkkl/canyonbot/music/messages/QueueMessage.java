package com.mkkl.canyonbot.music.messages;

import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.spec.EmbedCreateSpec;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;

import java.util.Iterator;

@Builder(setterPrefix = "set")
@Getter
public class QueueMessage<T extends TrackQueueElement> implements ResponseMessage {
    @Nonnull
    private TrackQueue<T> queue;
    private int page = 0;
    private int elementsPerPage = 10;

    @Override
    public EmbedCreateSpec getSpec() {
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
        embedBuilder.title("Queue");
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<T> iterator = queue.iterator();
        for (int i = 0; i < page * elementsPerPage; i++) {
            if (!iterator.hasNext()) break;
            iterator.next();
        }
        for (int i = page * elementsPerPage; i < (page + 1) * elementsPerPage; i++) {
            if (i >= queue.size()) break;
            stringBuilder.append(i + 1).append(". ").append(iterator.next().getAudioTrack().getInfo().title).append("\n");
        }
        embedBuilder.description(stringBuilder.toString());
        return embedBuilder.build();
    }
}
