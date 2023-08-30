package com.mkkl.canyonbot.musicbot.player;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public interface MusicPlayer {
    AudioTrack getPlayingTrack();

    void playTrack(AudioTrack track);

    void stopTrack();

    void setPaused(boolean b);

    boolean isPaused();

    long getTrackPosition();

    void seekTo(long position);

    void setVolume(int volume);

    int getVolume();

    //TODO: events using reactor
//    void addListener(IPlayerEventListener listener);
//
//    void removeListener(IPlayerEventListener listener);
}
