package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayer;

public class PlayerPauseEvent extends MusicPlayerEvent{

    public PlayerPauseEvent(MusicPlayer musicPlayer) {
        super(musicPlayer);
    }
}
