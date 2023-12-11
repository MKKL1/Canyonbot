package com.mkkl.canyonbot.music.player.lavaplayer;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import com.mkkl.canyonbot.music.player.MusicBotEventDispatcher;
import com.mkkl.canyonbot.music.player.event.base.*;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class LavaPlayerEventAdapter extends AudioEventAdapter {
    private final GuildMusicBot guildMusicBotManager;
    private final MusicBotEventDispatcher eventDispatcher;

    public LavaPlayerEventAdapter(GuildMusicBot guildMusicBotManager, MusicBotEventDispatcher eventDispatcher) {
        this.guildMusicBotManager = guildMusicBotManager;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        eventDispatcher.publish(new PlayerPauseEvent(guildMusicBotManager));
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        eventDispatcher.publish(new PlayerResumeEvent(guildMusicBotManager));
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        eventDispatcher.publish(new TrackStartEvent(guildMusicBotManager, track));
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        eventDispatcher.publish(new TrackEndEvent(guildMusicBotManager, track, endReason));
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        eventDispatcher.publish(new TrackExceptionEvent(guildMusicBotManager, track, exception));
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        eventDispatcher.publish(new TrackStuckEvent(guildMusicBotManager, track, thresholdMs, null));
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        eventDispatcher.publish(new TrackStuckEvent(guildMusicBotManager, track, thresholdMs, stackTrace));
    }

    @Override
    public void onEvent(AudioEvent event) {
        super.onEvent(event);
    }
}
