package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.event.GuildEvent;
import lombok.Getter;

@Getter
public class MayStopPlayerEvent implements GuildEvent {
    private final long guildId;
    public MayStopPlayerEvent(long guildId) {
        this.guildId = guildId;
    }
}
