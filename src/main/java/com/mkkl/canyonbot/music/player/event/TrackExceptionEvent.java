package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;

@Getter
public class TrackExceptionEvent extends MusicPlayerEvent{
    private final AudioTrack track;
    private final FriendlyException exception;

    public TrackExceptionEvent(MusicPlayerBase musicPlayerBase, AudioTrack track, FriendlyException exception) {
        super(musicPlayerBase);
        this.track = track;
        this.exception = exception;
    }
}
