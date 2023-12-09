package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;

public class PlayerPauseEvent extends MusicPlayerEvent {

    public PlayerPauseEvent(GuildMusicBotManager guildMusicBotManager) {
        super(guildMusicBotManager);
    }
}
