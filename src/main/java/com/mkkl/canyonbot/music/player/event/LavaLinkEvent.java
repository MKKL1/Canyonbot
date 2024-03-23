package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.event.AbstractEvent;
import dev.arbjerg.lavalink.client.LavalinkNode;

public interface LavaLinkEvent extends AbstractEvent {
    LavalinkNode getNode();
}
