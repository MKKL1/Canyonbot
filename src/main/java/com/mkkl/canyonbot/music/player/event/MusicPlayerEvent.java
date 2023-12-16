package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import lombok.Getter;

@Getter
public abstract class MusicPlayerEvent {
    private final GuildMusicBot guildMusicBotManager;
    protected MusicPlayerEvent(GuildMusicBot guildMusicBotManager) {
        this.guildMusicBotManager = guildMusicBotManager;
    }
}
