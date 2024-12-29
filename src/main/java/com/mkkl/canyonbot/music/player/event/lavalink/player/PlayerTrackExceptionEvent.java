package com.mkkl.canyonbot.music.player.event.lavalink.player;

import com.mkkl.canyonbot.music.player.event.PlayerEvent;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.event.TrackExceptionEvent;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.client.player.TrackException;
import lombok.Getter;

@Getter
public class PlayerTrackExceptionEvent implements PlayerEvent {
    private final LavalinkNode node;
    private final long guildId;
    private final Track track;
    private final TrackException exception;
    public PlayerTrackExceptionEvent(LavalinkNode node, long guildId, Track track, TrackException exception) {
        this.node = node;
        this.guildId = guildId;
        this.track = track;
        this.exception = exception;
    }

    public PlayerTrackExceptionEvent(TrackExceptionEvent trackExceptionEvent) {
        this(trackExceptionEvent.getNode(), trackExceptionEvent.getGuildId(), trackExceptionEvent.getTrack(), trackExceptionEvent.getException());
    }
}
