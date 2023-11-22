package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.MusicPlayerBase;

public class PlayerPauseEvent extends MusicPlayerEvent{

    public PlayerPauseEvent(MusicPlayerBase musicPlayerBase) {
        super(musicPlayerBase);
    }
}
