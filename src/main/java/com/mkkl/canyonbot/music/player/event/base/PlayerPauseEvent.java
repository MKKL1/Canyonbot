package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import discord4j.core.object.entity.Guild;

public class PlayerPauseEvent extends MusicPlayerEvent {

    public PlayerPauseEvent(Guild guild) {
        super(guild);
    }
}
