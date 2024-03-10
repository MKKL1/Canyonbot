package com.mkkl.canyonbot.event;

import lombok.Getter;

public interface GuildEvent extends AbstractEvent {
    long getGuildId();
}
