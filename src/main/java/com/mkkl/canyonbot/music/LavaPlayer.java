package com.mkkl.canyonbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class LavaPlayer implements MusicPlayer{

    private final AudioPlayer audioPlayer;

    public LavaPlayer(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public AudioTrack getPlayingTrack() {
        return audioPlayer.getPlayingTrack();
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
}
