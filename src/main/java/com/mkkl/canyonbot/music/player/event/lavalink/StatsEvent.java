package com.mkkl.canyonbot.music.player.event.lavalink;

import com.mkkl.canyonbot.music.player.event.LavaLinkEvent;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.protocol.v4.Cpu;
import dev.arbjerg.lavalink.protocol.v4.FrameStats;
import dev.arbjerg.lavalink.protocol.v4.Memory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatsEvent implements LavaLinkEvent {
    private final LavalinkNode node;
    private final FrameStats frameStats;
    private final int players;
    private final int playingPlayers;
    private final long uptime;
    private final Memory memory;
    private final Cpu cpu;

    public StatsEvent(dev.arbjerg.lavalink.client.StatsEvent statsEvent) {
        this(statsEvent.getNode(), statsEvent.getFrameStats(), statsEvent.getPlayers(), statsEvent.getPlayingPlayers(), statsEvent.getUptime(), statsEvent.getMemory(), statsEvent.getCpu());
    }
}
