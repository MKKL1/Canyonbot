package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import lombok.Getter;

@Getter
public abstract class MusicPlayerEvent {
    private final MusicPlayerBase musicPlayerBase;
    protected MusicPlayerEvent(MusicPlayerBase musicPlayerBase) {
        this.musicPlayerBase = musicPlayerBase;
    }
}
