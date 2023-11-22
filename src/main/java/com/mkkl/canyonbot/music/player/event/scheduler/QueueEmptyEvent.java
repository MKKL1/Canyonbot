package com.mkkl.canyonbot.music.player.event.scheduler;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import lombok.Getter;

@Getter
public class QueueEmptyEvent extends MusicPlayerEvent {
    private final TrackQueue<TrackQueueElement> trackQueue;
    public QueueEmptyEvent(GuildMusicBotManager guildMusicBotManager, TrackQueue<TrackQueueElement> trackQueue) {
        super(guildMusicBotManager);
        this.trackQueue = trackQueue;
    }
}
