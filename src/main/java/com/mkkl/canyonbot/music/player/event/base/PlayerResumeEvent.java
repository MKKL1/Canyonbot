package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import discord4j.core.object.entity.Guild;

public class PlayerResumeEvent extends MusicPlayerEvent {
    public PlayerResumeEvent(Guild guild) {
        super(guild);
    }
}
