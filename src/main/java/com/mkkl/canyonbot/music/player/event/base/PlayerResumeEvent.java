package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.MusicPlayerBase;

public class PlayerResumeEvent extends MusicPlayerEvent{
    public PlayerResumeEvent(MusicPlayerBase musicPlayerBase) {
        super(musicPlayerBase);
    }
}
