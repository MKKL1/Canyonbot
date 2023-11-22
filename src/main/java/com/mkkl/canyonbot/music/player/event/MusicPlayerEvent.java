package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import lombok.Getter;

@Getter
public abstract class MusicPlayerEvent {
    private final GuildMusicBotManager guildMusicBotManager;
    protected MusicPlayerEvent(GuildMusicBotManager guildMusicBotManager) {
        this.guildMusicBotManager = guildMusicBotManager;
    }
}
