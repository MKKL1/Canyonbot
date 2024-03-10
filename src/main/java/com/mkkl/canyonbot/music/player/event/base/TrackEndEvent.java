package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import discord4j.core.object.entity.Guild;
import lombok.Getter;

@Getter
public class TrackEndEvent extends MusicPlayerEvent {
    private final AudioTrack track;
    private final AudioTrackEndReason endReason;
    public TrackEndEvent(Guild guild, AudioTrack track, AudioTrackEndReason endReason) {
        super(guild);
        this.track = track;
        this.endReason = endReason;
    }
}
