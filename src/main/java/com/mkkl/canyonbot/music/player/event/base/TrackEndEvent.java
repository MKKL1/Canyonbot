package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;

@Getter
public class TrackEndEvent extends MusicPlayerEvent {
    private final AudioTrack track;
    private final AudioTrackEndReason endReason;
    public TrackEndEvent(GuildMusicBotManager guildMusicBotManager, AudioTrack track, AudioTrackEndReason endReason) {
        super(guildMusicBotManager);
        this.track = track;
        this.endReason = endReason;
    }
}
