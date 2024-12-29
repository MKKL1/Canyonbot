package com.mkkl.canyonbot.music.player.queue;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

//Just a simple LinkedBlockingQueue adapter
public class SimpleTrackQueue implements TrackQueue {

    private final Queue<TrackQueueElement> queue = new LinkedBlockingQueue<>();

    @Override
    public TrackQueueElement dequeue() {
        return queue.poll();
    }

    @Override
    public void shuffle() {
        List<TrackQueueElement> trackQueueElements = new ArrayList<>(queue);
        Collections.shuffle(trackQueueElements);
        queue.clear();
        queue.addAll(trackQueueElements);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public Iterator<TrackQueueElement> iterator() {
        return queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    }

    @Override
    public boolean add(TrackQueueElement trackQueueElement) {
        return queue.add(trackQueueElement);
    }

    @Override
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends TrackQueueElement> c) {
        return queue.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return queue.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return queue.retainAll(c);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean equals(Object o) {
        return queue.equals(o);
    }

    @Override
    public int hashCode() {
        return queue.hashCode();
    }
}
