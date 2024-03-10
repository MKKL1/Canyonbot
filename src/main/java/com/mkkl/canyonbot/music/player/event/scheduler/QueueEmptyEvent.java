package com.mkkl.canyonbot.music.player.event.scheduler;

import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import discord4j.core.object.entity.Guild;
import lombok.Getter;

@Getter
public class QueueEmptyEvent extends MusicPlayerEvent {
    private final TrackQueue trackQueue;
    public QueueEmptyEvent(Guild guild, TrackQueue trackQueue) {
        super(guild);
        this.trackQueue = trackQueue;
    }
}
