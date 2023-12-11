package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;

public class PlayerPauseEvent extends MusicPlayerEvent {

    public PlayerPauseEvent(GuildMusicBot guildMusicBotManager) {
        super(guildMusicBotManager);
    }
}
