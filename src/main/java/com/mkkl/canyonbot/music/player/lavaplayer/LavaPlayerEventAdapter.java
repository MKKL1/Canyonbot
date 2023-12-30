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
import discord4j.core.object.entity.Guild;

public class LavaPlayerEventAdapter extends AudioEventAdapter {
    private final Guild guild;
    private final MusicBotEventDispatcher eventDispatcher;

    public LavaPlayerEventAdapter(Guild guild, MusicBotEventDispatcher eventDispatcher) {
        this.guild = guild;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        eventDispatcher.publish(new PlayerPauseEvent(guild));
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        eventDispatcher.publish(new PlayerResumeEvent(guild));
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        eventDispatcher.publish(new TrackStartEvent(guild, track));
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        eventDispatcher.publish(new TrackEndEvent(guild, track, endReason));
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        eventDispatcher.publish(new TrackExceptionEvent(guild, track, exception));
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        eventDispatcher.publish(new TrackStuckEvent(guild, track, thresholdMs, null));
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        eventDispatcher.publish(new TrackStuckEvent(guild, track, thresholdMs, stackTrace));
    }

    @Override
    public void onEvent(AudioEvent event) {
        super.onEvent(event);
    }
}
