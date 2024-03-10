package com.mkkl.canyonbot.music.player.event;

import discord4j.core.object.entity.Guild;
import lombok.Getter;

public abstract class MusicPlayerEvent {
    private final Guild guild;
    protected MusicPlayerEvent(Guild guild) {
        this.guild = guild;
    }
}
