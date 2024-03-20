package com.mkkl.canyonbot.music.player.event.lavalink;

import com.mkkl.canyonbot.event.AbstractEvent;
import com.mkkl.canyonbot.music.player.event.lavalink.player.PlayerTrackEndEvent;
import com.mkkl.canyonbot.music.player.event.lavalink.player.PlayerTrackExceptionEvent;
import com.mkkl.canyonbot.music.player.event.lavalink.player.PlayerTrackStartEvent;
import com.mkkl.canyonbot.music.player.event.lavalink.player.PlayerTrackStuckEvent;
import dev.arbjerg.lavalink.client.ClientEvent;
import dev.arbjerg.lavalink.client.PlayerUpdateEvent;
import dev.arbjerg.lavalink.client.ReadyEvent;
import dev.arbjerg.lavalink.client.StatsEvent;
import dev.arbjerg.lavalink.client.TrackEndEvent;
import dev.arbjerg.lavalink.client.TrackExceptionEvent;
import dev.arbjerg.lavalink.client.TrackStartEvent;
import dev.arbjerg.lavalink.client.TrackStuckEvent;
import dev.arbjerg.lavalink.client.WebSocketClosedEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class LavalinkEventAdapter {
    // This is a map of the Lavalink events to the CanyonBot events
    // Don't touch!
    private static final Map<Class<?>, Function<ClientEvent<?>, AbstractEvent>> eventMap = new ConcurrentHashMap<>();
    static {
        eventMap.put(TrackEndEvent.class, (event) -> new PlayerTrackEndEvent((TrackEndEvent) event));
        eventMap.put(TrackExceptionEvent.class, (event) -> new PlayerTrackExceptionEvent((TrackExceptionEvent) event));
        eventMap.put(TrackStartEvent.class, (event) -> new PlayerTrackStartEvent((TrackStartEvent) event));
        eventMap.put(TrackStuckEvent.class, (event) -> new PlayerTrackStuckEvent((TrackStuckEvent) event));
        eventMap.put(WebSocketClosedEvent.class, (event) -> new com.mkkl.canyonbot.music.player.event.lavalink.player.WebSocketClosedEvent((WebSocketClosedEvent) event));
        eventMap.put(PlayerUpdateEvent.class, (event) -> new com.mkkl.canyonbot.music.player.event.lavalink.PlayerUpdateEvent((PlayerUpdateEvent) event));
        eventMap.put(ReadyEvent.class, (event) -> new com.mkkl.canyonbot.music.player.event.lavalink.ReadyEvent((ReadyEvent) event));
        eventMap.put(StatsEvent.class, (event) -> new com.mkkl.canyonbot.music.player.event.lavalink.StatsEvent((StatsEvent) event));
    }
    public static AbstractEvent get(ClientEvent<?> event) {
        return eventMap.get(event.getClass()).apply(event);
    }
}
