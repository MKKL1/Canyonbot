package com.mkkl.canyonbot.music.player.queue;

import java.util.Collection;

//Represents a collection of tracks to be played
public interface TrackQueue<T extends TrackQueueElement> extends Collection<T> {
    boolean enqueue(T track);
    T dequeue();

    default boolean add(T track) {
        return enqueue(track);
    }
}
