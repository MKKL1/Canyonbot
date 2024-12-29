package com.mkkl.canyonbot.music.player.event.lavalink.player;

import com.mkkl.canyonbot.music.player.event.PlayerEvent;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.event.TrackEndEvent;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.protocol.v4.Message;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PlayerTrackEndEvent implements PlayerEvent {
    private final LavalinkNode node;
    private final long guildId;
    private final Track track;
    private final Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason reason;
    public PlayerTrackEndEvent(LavalinkNode node, long guildId, Track track, Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason reason) {
        this.node = node;
        this.guildId = guildId;
        this.track = track;
        this.reason = reason;
    }

    public PlayerTrackEndEvent(TrackEndEvent trackEndEvent) {
        this(trackEndEvent.getNode(), trackEndEvent.getGuildId(), trackEndEvent.getTrack(), trackEndEvent.getEndReason());
    }

}
