package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;

@Getter
public class TrackStartEvent extends MusicPlayerEvent{
    private final AudioTrack track;
    public TrackStartEvent(MusicPlayerBase musicPlayerBase, AudioTrack track) {
        super(musicPlayerBase);
        this.track = track;
    }
}
