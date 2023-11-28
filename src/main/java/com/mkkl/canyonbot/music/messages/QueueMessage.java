package com.mkkl.canyonbot.music.messages;

import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.*;

@Builder(setterPrefix = "set")
@Getter
public class QueueMessage<T extends TrackQueueElement> implements ResponseMessage {
    @Nonnull
    private TrackQueue<T> queue;
    private int page = 0;
    private int elementsPerPage = 10;
    private User caller;



    @Override
    public EmbedCreateSpec getSpec() {
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
        embedBuilder.title("Queue");
        //TODO this way of skipping thru queue is kind of inefficient
        Iterator<T> iterator = queue.iterator();
        for (int i = 0; i < page * elementsPerPage; i++) {
            if (!iterator.hasNext()) break;
            iterator.next();
        }
        FieldTable.Builder fieldTable = FieldTable.builder()
                .addHeader("Pos")
                .addHeader("Title")
                .addHeader("Duration")
                .addHeader("Requested by");

        for (int i = page * elementsPerPage; i < (page + 1) * elementsPerPage; i++) {
            if (i >= queue.size()) break;
            T track = iterator.next();
            fieldTable.addRow(
                    String.valueOf(i + 1),
                    track.getAudioTrack().getInfo().title,
                    ResponseFormatUtils.formatDuration(track.getAudioTrack().getDuration()),
                    track.getUser().getMention()
            );
        }
        embedBuilder.fields(fieldTable.build().getFields());

        embedBuilder.timestamp(Instant.now());
        embedBuilder.footer(caller.getUsername(), caller.getAvatarUrl());
        return embedBuilder.build();
    }
}
