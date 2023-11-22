package com.mkkl.canyonbot.music.player.event.base;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;

@Getter
public class TrackStartEvent extends MusicPlayerEvent {
    private final AudioTrack track;
    public TrackStartEvent(GuildMusicBotManager guildMusicBotManager, AudioTrack track) {
        super(guildMusicBotManager);
        this.track = track;
    }
}
