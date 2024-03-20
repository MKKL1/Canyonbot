package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.event.GuildEvent;
import lombok.Getter;

@Getter
public class LinkContextCreationEvent implements GuildEvent {
    private final long guildId;

    public LinkContextCreationEvent(long guildId) {
        this.guildId = guildId;
    }
}
