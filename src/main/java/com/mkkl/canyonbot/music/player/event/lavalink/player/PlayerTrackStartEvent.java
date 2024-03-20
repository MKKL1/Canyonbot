package com.mkkl.canyonbot.music.player.event.lavalink.player;

import com.mkkl.canyonbot.music.player.event.PlayerEvent;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.TrackStartEvent;
import dev.arbjerg.lavalink.client.protocol.Track;
import lombok.Getter;

@Getter
public class PlayerTrackStartEvent implements PlayerEvent {
    private final LavalinkNode node;
    private final long guildId;
    private final Track track;
    public PlayerTrackStartEvent(LavalinkNode node, long guildId, Track track) {
        this.node = node;
        this.guildId = guildId;
        this.track = track;
    }

    public PlayerTrackStartEvent(TrackStartEvent trackStartEvent) {
        this(trackStartEvent.getNode(), trackStartEvent.getGuildId(), trackStartEvent.getTrack());
    }
}
