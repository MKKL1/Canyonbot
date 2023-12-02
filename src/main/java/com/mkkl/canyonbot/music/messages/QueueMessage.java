package com.mkkl.canyonbot.music.messages;

import com.mkkl.canyonbot.discord.interaction.TempListenableButton;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeoutException;

//TODO this part of code requires massive refactoring
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
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<T> iterator = queue.iterator();
        for (int i = 0; i < page * elementsPerPage; i++) {
            if (!iterator.hasNext()) break;
            iterator.next();
        }
        for (int i = page * elementsPerPage; i < (page + 1) * elementsPerPage; i++) {
            if (i >= queue.size()) break;
            stringBuilder.append(i + 1)
                    .append(". ")
                    .append(iterator.next()
                            .getAudioTrack()
                            .getInfo().title)
                    .append("\n");
        }
        embedBuilder.description(stringBuilder.toString());
        embedBuilder.timestamp(Instant.now());
        embedBuilder.footer(caller.getUsername(), caller.getAvatarUrl());
        return embedBuilder.build();
    }

    public ActionRow getActionRow() {
        TempListenableButton nextPageButton = TempListenableButton.builder(Button.secondary("nextPage", "Next page"))
                .setButtonClickAction(event -> {
                    page++;
                    event.getMessage().get().getData().
                    return event.edit("Queue");
                })
                .setTimeoutAction(throwable -> {
                    System.out.println("timeout");
                    return Mono.empty();
                })
                .build();
        nextPageButton.register(Duration.ofSeconds(30))
                .subscribe();//TODO subscribing here means that errors are not caught
        return ActionRow.of(nextPageButton.getButton());
    }
}
