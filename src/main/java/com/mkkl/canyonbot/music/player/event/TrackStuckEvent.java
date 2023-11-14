package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class TrackStuckEvent extends MusicPlayerEvent{
    private final AudioTrack track;
    private final long thresholdMs;
    @Nullable
    private final StackTraceElement[] stackTrace;

    public TrackStuckEvent(MusicPlayerBase musicPlayerBase, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        super(musicPlayerBase);
        this.track = track;
        this.thresholdMs = thresholdMs;
        this.stackTrace = stackTrace;
    }
}
