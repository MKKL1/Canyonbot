package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;

@Getter
public class TrackEndEvent extends MusicPlayerEvent{
    private final AudioTrack track;
    private final AudioTrackEndReason endReason;
    public TrackEndEvent(MusicPlayerBase musicPlayerBase, AudioTrack track, AudioTrackEndReason endReason) {
        super(musicPlayerBase);
        this.track = track;
        this.endReason = endReason;
    }
}
