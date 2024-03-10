package com.mkkl.canyonbot.music.player.event.scheduler;

import com.mkkl.canyonbot.music.player.event.PlayerEvent;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import discord4j.core.object.entity.Guild;
import lombok.Getter;

@Getter
public class QueueEmptyEvent extends PlayerEvent {
    private final TrackQueue trackQueue;
    public QueueEmptyEvent(Guild guild, TrackQueue trackQueue) {
        super(guild);
        this.trackQueue = trackQueue;
    }
}
