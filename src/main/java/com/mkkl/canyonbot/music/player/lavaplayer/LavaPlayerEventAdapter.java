package com.mkkl.canyonbot.music.player.lavaplayer;

import com.mkkl.canyonbot.music.player.MusicPlayer;
import com.mkkl.canyonbot.music.player.event.*;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import reactor.core.publisher.FluxSink;

public class LavaPlayerEventAdapter extends AudioEventAdapter {
    private final MusicPlayer musicPlayer;
    private final FluxSink<MusicPlayerEvent> sink;
    public LavaPlayerEventAdapter(MusicPlayer musicPlayer, FluxSink<MusicPlayerEvent> sink) {
        super();
        this.musicPlayer = musicPlayer;
        this.sink = sink;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        sink.next(new PlayerPauseEvent(musicPlayer));
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        sink.next(new PlayerResumeEvent(musicPlayer));
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        sink.next(new TrackStartEvent(musicPlayer, track));
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        sink.next(new TrackEndEvent(musicPlayer, track, endReason));
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        sink.next(new TrackExceptionEvent(musicPlayer, track, exception));
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        sink.next(new TrackStuckEvent(musicPlayer, track, thresholdMs, null));
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        sink.next(new TrackStuckEvent(musicPlayer, track, thresholdMs, stackTrace));
    }

    @Override
    public void onEvent(AudioEvent event) {
        super.onEvent(event);
    }
}
