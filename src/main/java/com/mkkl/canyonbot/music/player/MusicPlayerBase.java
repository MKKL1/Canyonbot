package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.voice.AudioProvider;
import reactor.core.publisher.FluxSink;

//Interface for most basic music player functionality (without queue, voice channel management, etc.)
public interface MusicPlayerBase {
    AudioProvider createAudioProvider();
    AudioTrack getPlayingTrack();

    void registerEvents(GuildMusicBotManager guildMusicBotManager, MusicBotEventDispatcher eventDispatcher);

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