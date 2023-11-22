package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;

public class PlayerResumeEvent extends MusicPlayerEvent {
    public PlayerResumeEvent(GuildMusicBotManager guildMusicBotManager) {
        super(guildMusicBotManager);
    }
}
