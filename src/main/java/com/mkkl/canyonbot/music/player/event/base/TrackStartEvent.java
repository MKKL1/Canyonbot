package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.Guild;
import lombok.Getter;

@Getter
public class TrackStartEvent extends MusicPlayerEvent {
    private final AudioTrack track;
    public TrackStartEvent(Guild guild, AudioTrack track) {
        super(guild);
        this.track = track;
    }
}
