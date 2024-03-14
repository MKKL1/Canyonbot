package com.mkkl.canyonbot.music.player.event.scheduler;

import com.mkkl.canyonbot.event.GuildEvent;
import com.mkkl.canyonbot.music.player.event.PlayerEvent;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import dev.arbjerg.lavalink.client.LavalinkNode;
import discord4j.core.object.entity.Guild;
import lombok.Getter;

@Getter
public class QueueEmptyEvent implements GuildEvent {
    private final long guildId;
    private final TrackQueue trackQueue;
    public QueueEmptyEvent(long guildId, TrackQueue trackQueue) {
        this.guildId = guildId;
        this.trackQueue = trackQueue;
    }
}
