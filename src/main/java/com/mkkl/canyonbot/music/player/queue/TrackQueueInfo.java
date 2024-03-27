package com.mkkl.canyonbot.music.player.queue;

import com.mkkl.canyonbot.music.player.TrackScheduler;
import lombok.Data;

import java.util.List;

@Data
public class TrackQueueInfo {
    private final List<TrackQueueElement> trackQueue;
    private final TrackQueueElement currentTrack;
    private final TrackScheduler.State schedulerState;
}
