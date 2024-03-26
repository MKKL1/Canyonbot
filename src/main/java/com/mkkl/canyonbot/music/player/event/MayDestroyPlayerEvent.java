package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.event.GuildEvent;
import lombok.Getter;

@Getter
public class MayDestroyPlayerEvent implements GuildEvent {
    private final long guildId;
    public MayDestroyPlayerEvent(long guildId) {
        this.guildId = guildId;
    }
}
