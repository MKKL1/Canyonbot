package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;

@Getter
public class TrackEndEvent extends MusicPlayerEvent{
    private final AudioTrack track;
    private final AudioTrackEndReason endReason;
    public TrackEndEvent(MusicPlayer musicPlayer, AudioTrack track, AudioTrackEndReason endReason) {
        super(musicPlayer);
        this.track = track;
        this.endReason = endReason;
    }
}
