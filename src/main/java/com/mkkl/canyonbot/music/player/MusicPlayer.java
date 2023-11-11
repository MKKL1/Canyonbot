package com.mkkl.canyonbot.music.player;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.Guild;
import discord4j.voice.AudioProvider;
import org.springframework.stereotype.Component;
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

    Flux<AudioEvent> getEventFlux();

    //TODO: events using reactor
    void addListener(AudioEventListener listener);

    void removeListener(AudioEventListener listener);
}