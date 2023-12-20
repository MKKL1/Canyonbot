package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import lombok.Getter;

@Getter
public class GuildPlayerCreationEvent extends PlayerApplicationEvent {
    private final GuildMusicBot guildMusicBot;
    public GuildPlayerCreationEvent(Object source, GuildMusicBot guildMusicBot) {
        super(source);
        this.guildMusicBot = guildMusicBot;
    }
}
