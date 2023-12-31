package com.mkkl.canyonbot.music.player;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.Guild;
import discord4j.voice.AudioProvider;
import org.springframework.stereotype.Service;

//Interface for most basic music player functionality (without queue, voice channel management, etc.)
public interface MusicPlayerBase {
    AudioProvider getAudioProvider();
    AudioTrack getPlayingTrack();

    void registerEvents(Guild guild, MusicBotEventDispatcher eventDispatcher);

    void playTrack(AudioTrack track);

    void stopTrack();

    void setPaused(boolean b);

    boolean isPaused();

    long getTrackPosition();

    void seekTo(long position);

    void setVolume(int volume);

    int getVolume();

    void destroy();
}