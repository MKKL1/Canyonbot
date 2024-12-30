package com.mkkl.canyonbot.music.player.event.lavalink;

import com.mkkl.canyonbot.event.AbstractEvent;
import com.mkkl.canyonbot.music.player.event.lavalink.player.PlayerTrackEndEvent;
import com.mkkl.canyonbot.music.player.event.lavalink.player.PlayerTrackExceptionEvent;
import com.mkkl.canyonbot.music.player.event.lavalink.player.PlayerTrackStartEvent;
import com.mkkl.canyonbot.music.player.event.lavalink.player.PlayerTrackStuckEvent;
import dev.arbjerg.lavalink.client.event.*;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class LavalinkEventAdapter {
    // This is a map of the Lavalink events to the CanyonBot events
    private static final Map<Class<?>, Function<ClientEvent, AbstractEvent>> eventMap = new ConcurrentHashMap<>();
    static {
        eventMap.put(TrackEndEvent.class, (event) -> new PlayerTrackEndEvent((TrackEndEvent) event));
        eventMap.put(TrackExceptionEvent.class, (event) -> new PlayerTrackExceptionEvent((TrackExceptionEvent) event));
        eventMap.put(TrackStartEvent.class, (event) -> new PlayerTrackStartEvent((TrackStartEvent) event));
        eventMap.put(TrackStuckEvent.class, (event) -> new PlayerTrackStuckEvent((TrackStuckEvent) event));
        eventMap.put(dev.arbjerg.lavalink.client.event.WebSocketClosedEvent.class, (event) -> new com.mkkl.canyonbot.music.player.event.lavalink.player.WebSocketClosedEvent((dev.arbjerg.lavalink.client.event.WebSocketClosedEvent) event));
        eventMap.put(dev.arbjerg.lavalink.client.event.PlayerUpdateEvent.class, (event) -> new PlayerUpdateEvent((dev.arbjerg.lavalink.client.event.PlayerUpdateEvent) event));
        eventMap.put(dev.arbjerg.lavalink.client.event.ReadyEvent.class, (event) -> new ReadyEvent((dev.arbjerg.lavalink.client.event.ReadyEvent) event));
        eventMap.put(dev.arbjerg.lavalink.client.event.StatsEvent.class, (event) -> new StatsEvent((dev.arbjerg.lavalink.client.event.StatsEvent) event));
    }

    public static AbstractEvent get(ClientEvent event) {
        return Optional.ofNullable(eventMap.get(event.getClass())).orElseThrow(() -> new NoSuchElementException("Unknown event " + event.getClass().getCanonicalName())).apply(event);
    }
}
