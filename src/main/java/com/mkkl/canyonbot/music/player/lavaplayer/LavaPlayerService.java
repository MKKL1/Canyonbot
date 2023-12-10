package com.mkkl.canyonbot.music.player.lavaplayer;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.MusicBotEventDispatcher;
import com.mkkl.canyonbot.music.player.MusicPlayerBaseService;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.voice.AudioProvider;

public class LavaPlayerService implements MusicPlayerBaseService {

    private final AudioPlayer audioPlayer;
    private final LavaPlayerAudioProvider audioProvider;

    public LavaPlayerService(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        audioProvider = new LavaPlayerAudioProvider(audioPlayer);
    }

    @Override
    public AudioProvider getAudioProvider() {
        return audioProvider;
    }

    @Override
    public AudioTrack getPlayingTrack() {
        return audioPlayer.getPlayingTrack();
    }

    @Override
    public void registerEvents(GuildMusicBotManager guildMusicBotManager, MusicBotEventDispatcher eventDispatcher) {
        audioPlayer.addListener(new LavaPlayerEventAdapter(guildMusicBotManager, eventDispatcher));
    }

    @Override
    public void playTrack(AudioTrack track) {
        audioPlayer.playTrack(track);
    }

    @Override
    public void stopTrack() {
        audioPlayer.stopTrack();
    }

    @Override
    public void setPaused(boolean b) {
        audioPlayer.setPaused(b);
    }

    @Override
    public boolean isPaused() {
        return audioPlayer.isPaused();
    }

    @Override
    public long getTrackPosition() {
        if (audioPlayer.getPlayingTrack() == null) throw new IllegalStateException("Not playing anything");

        return audioPlayer.getPlayingTrack().getPosition();
    }

    @Override
    public void seekTo(long position) {
        if (audioPlayer.getPlayingTrack() == null) throw new IllegalStateException("Not playing anything");

        audioPlayer.getPlayingTrack().setPosition(position);
    }

    @Override
    public void setVolume(int volume) {
        audioPlayer.setVolume(volume);
    }

    @Override
    public int getVolume() {
        return audioPlayer.getVolume();
    }

    @Override
    public void destroy() {
        audioPlayer.destroy();
    }


}
