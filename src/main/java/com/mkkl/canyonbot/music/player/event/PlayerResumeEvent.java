package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayer;

public class PlayerResumeEvent extends MusicPlayerEvent{
    public PlayerResumeEvent(MusicPlayer musicPlayer) {
        super(musicPlayer);
    }
}
