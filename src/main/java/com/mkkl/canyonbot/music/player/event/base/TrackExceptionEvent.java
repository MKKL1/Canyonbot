package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.Guild;
import lombok.Getter;

@Getter
public class TrackExceptionEvent extends MusicPlayerEvent {
    private final AudioTrack track;
    private final FriendlyException exception;

    public TrackExceptionEvent(Guild guild, AudioTrack track, FriendlyException exception) {
        super(guild);
        this.track = track;
        this.exception = exception;
    }
}
