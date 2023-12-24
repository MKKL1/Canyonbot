package com.mkkl.canyonbot.music.player.event.scheduler;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import lombok.Getter;

@Getter
public class QueueEmptyEvent extends MusicPlayerEvent {
    private final TrackQueue trackQueue;
    public QueueEmptyEvent(GuildMusicBot guildMusicBotManager, TrackQueue trackQueue) {
        super(guildMusicBotManager);
        this.trackQueue = trackQueue;
    }
}
