package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.event.GuildEvent;
import lombok.Getter;

@Getter
public class LinkContextDestroyEvent implements GuildEvent {
    private final long guildId;

    public LinkContextDestroyEvent(long guildId) {
        this.guildId = guildId;
    }
}
