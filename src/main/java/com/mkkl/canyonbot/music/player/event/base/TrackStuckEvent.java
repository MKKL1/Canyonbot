package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.Guild;
import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class TrackStuckEvent extends MusicPlayerEvent {
    private final AudioTrack track;
    private final long thresholdMs;
    @Nullable
    private final StackTraceElement[] stackTrace;

    public TrackStuckEvent(Guild guild, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        super(guild);
        this.track = track;
        this.thresholdMs = thresholdMs;
        this.stackTrace = stackTrace;
    }
}
