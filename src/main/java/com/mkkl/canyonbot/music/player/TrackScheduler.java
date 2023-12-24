package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
public class TrackScheduler {
    @Setter
    @Nullable
    private TrackQueueElement currentTrack = null;
    @Setter
    private State state = State.STOPPED;
    private final GuildMusicBot guildMusicBot;

    public TrackScheduler(GuildMusicBot guildMusicBot) {
        this.guildMusicBot = guildMusicBot;
    }

    public enum State {
        PLAYING,
        PAUSED,
        STOPPED
    }
}
