package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.queue.SimpleTrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.object.entity.Guild;
import discord4j.voice.AudioProvider;
import lombok.Getter;

@Getter
public class GuildMusicPlayer {
    private final AudioProvider audioProvider;
    private final Guild guild;
    private final TrackQueue<TrackQueueElement> trackQueue = new SimpleTrackQueue();

    public GuildMusicPlayer(AudioProvider audioProvider, Guild guild) {
        this.audioProvider = audioProvider;
        this.guild = guild;
    }
}
