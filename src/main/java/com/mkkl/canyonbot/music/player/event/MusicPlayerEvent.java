package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayer;
import discord4j.core.object.entity.channel.VoiceChannel;
import lombok.Getter;

@Getter
public abstract class MusicPlayerEvent {
    private final MusicPlayer musicPlayer;
    protected MusicPlayerEvent(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }
}
