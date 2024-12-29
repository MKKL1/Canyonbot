package com.mkkl.canyonbot.music.player.event.lavalink.player;

import com.mkkl.canyonbot.music.player.event.PlayerEvent;
import dev.arbjerg.lavalink.client.LavalinkNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebSocketClosedEvent implements PlayerEvent {
    private final LavalinkNode node;
    private final long guildId;
    private final int code;
    private final String reason;
    private final boolean byRemote;

    public WebSocketClosedEvent(dev.arbjerg.lavalink.client.event.WebSocketClosedEvent webSocketClosedEvent) {
        this(webSocketClosedEvent.getNode(), webSocketClosedEvent.getGuildId(), webSocketClosedEvent.getCode(), webSocketClosedEvent.getReason(), webSocketClosedEvent.getByRemote());
    }
}
