package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;

@Getter
public class TrackExceptionEvent extends MusicPlayerEvent{
    private final AudioTrack track;
    private final FriendlyException exception;

    public TrackExceptionEvent(MusicPlayer musicPlayer, AudioTrack track, FriendlyException exception) {
        super(musicPlayer);
        this.track = track;
        this.exception = exception;
    }
}
