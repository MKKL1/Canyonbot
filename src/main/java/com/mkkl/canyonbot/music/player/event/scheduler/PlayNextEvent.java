package com.mkkl.canyonbot.music.player.event.scheduler;

import com.mkkl.canyonbot.event.GuildEvent;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayNextEvent implements GuildEvent {
    private final long guildId;
    private final TrackQueue trackQueue;
    private final TrackQueueElement track;
}
