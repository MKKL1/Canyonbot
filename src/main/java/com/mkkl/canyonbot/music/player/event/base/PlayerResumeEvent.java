package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;

public class PlayerResumeEvent extends MusicPlayerEvent {
    public PlayerResumeEvent(GuildMusicBot guildMusicBotManager) {
        super(guildMusicBotManager);
    }
}
