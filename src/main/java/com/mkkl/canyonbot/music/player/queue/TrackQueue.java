package com.mkkl.canyonbot.music.player.queue;

import jakarta.annotation.Nullable;

import java.util.Collection;

//Represents a collection of tracks to be played
public interface TrackQueue extends Collection<TrackQueueElement> {
    @Nullable
    TrackQueueElement dequeue();

    void shuffle();
}
