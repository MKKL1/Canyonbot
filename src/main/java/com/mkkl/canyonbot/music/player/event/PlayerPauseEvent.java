package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayerBase;

public class PlayerPauseEvent extends MusicPlayerEvent{

    public PlayerPauseEvent(MusicPlayerBase musicPlayerBase) {
        super(musicPlayerBase);
    }
}
