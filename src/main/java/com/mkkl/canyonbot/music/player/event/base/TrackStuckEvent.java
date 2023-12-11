package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class TrackStuckEvent extends MusicPlayerEvent {
    private final AudioTrack track;
    private final long thresholdMs;
    @Nullable
    private final StackTraceElement[] stackTrace;

    public TrackStuckEvent(GuildMusicBot guildMusicBotManager, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        super(guildMusicBotManager);
        this.track = track;
        this.thresholdMs = thresholdMs;
        this.stackTrace = stackTrace;
    }
}
