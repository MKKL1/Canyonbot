package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.voice.AudioProvider;
import reactor.core.publisher.Flux;

public interface MusicPlayer {
    AudioProvider getAudioProvider();
    AudioTrack getPlayingTrack();

    void playTrack(AudioTrack track);

    void stopTrack();

    void setPaused(boolean b);

    boolean isPaused();

    long getTrackPosition();

    void seekTo(long position);

    void setVolume(int volume);

    int getVolume();
    <E extends MusicPlayerEvent> Flux<E> on(Class<E> clazz);
}