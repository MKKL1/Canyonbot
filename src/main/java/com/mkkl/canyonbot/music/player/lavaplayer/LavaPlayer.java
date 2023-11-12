package com.mkkl.canyonbot.music.player.lavaplayer;

import com.mkkl.canyonbot.music.player.MusicPlayer;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.voice.AudioProvider;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class LavaPlayer implements MusicPlayer {

    private final AudioPlayer audioPlayer;
    private final Flux<MusicPlayerEvent> eventFlux;
    private final LavaPlayerAudioProvider audioProvider;
    private static final Scheduler scheduler = Schedulers.boundedElastic();

    public LavaPlayer(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        eventFlux = Flux.create(sink -> audioPlayer.addListener(new LavaPlayerEventAdapter(LavaPlayer.this, sink)));
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
    public <E extends MusicPlayerEvent> Flux<E> on(Class<E> clazz) {
        return eventFlux.publishOn(scheduler).ofType(clazz);
    }

}
