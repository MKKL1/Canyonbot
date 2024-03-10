package com.mkkl.canyonbot.music.player.event.lavalink.player;

import com.mkkl.canyonbot.music.player.event.PlayerEvent;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.TrackStuckEvent;
import dev.arbjerg.lavalink.client.protocol.Track;
import lombok.Getter;

@Getter
public class PlayerTrackStuckEvent implements PlayerEvent {
    private final LavalinkNode node;
    private final long guildId;
    private final Track track;
    public PlayerTrackStuckEvent(LavalinkNode node, long guildId, Track track, long tresholdMs) {
        this.node = node;
        this.guildId = guildId;
        this.track = track;
    }

    public PlayerTrackStuckEvent(TrackStuckEvent trackStuckEvent) {
        this(trackStuckEvent.getNode(), trackStuckEvent.getGuildId(), trackStuckEvent.getTrack(), trackStuckEvent.getThresholdMs());
    }
}
