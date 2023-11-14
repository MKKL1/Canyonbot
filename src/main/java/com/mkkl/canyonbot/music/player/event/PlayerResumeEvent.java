package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayerBase;

public class PlayerResumeEvent extends MusicPlayerEvent{
    public PlayerResumeEvent(MusicPlayerBase musicPlayerBase) {
        super(musicPlayerBase);
    }
}
