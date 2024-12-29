package com.mkkl.canyonbot.music.player.event.lavalink;

import com.mkkl.canyonbot.music.player.event.LavaLinkEvent;
import dev.arbjerg.lavalink.client.LavalinkNode;
import lombok.Getter;

@Getter
public class ReadyEvent implements LavaLinkEvent {
    private final LavalinkNode node;
    private final boolean resumed;
    private final String sessionId;

    public ReadyEvent(LavalinkNode node, boolean resumed, String sessionId) {
        this.node = node;
        this.resumed = resumed;
        this.sessionId = sessionId;
    }

    public ReadyEvent(dev.arbjerg.lavalink.client.event.ReadyEvent readyEvent) {
        this(readyEvent.getNode(), readyEvent.getResumed(), readyEvent.getSessionId());
    }
}
