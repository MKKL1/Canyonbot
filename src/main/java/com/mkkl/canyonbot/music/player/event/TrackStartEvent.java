package com.mkkl.canyonbot.music.player.event;

import com.mkkl.canyonbot.music.player.MusicPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class TrackStartEvent extends MusicPlayerEvent{
    private final AudioTrack track;
    public TrackStartEvent(MusicPlayer musicPlayer, AudioTrack track) {
        super(musicPlayer);
        this.track = track;
    }
}
