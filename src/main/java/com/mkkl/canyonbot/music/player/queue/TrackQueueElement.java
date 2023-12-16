package com.mkkl.canyonbot.music.player.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;

//TODO change name to something more descriptive
@Getter
@AllArgsConstructor
public class TrackQueueElement {
    private final AudioTrack audioTrack;
    private final User user;

    public static TrackQueueElement of(AudioTrack audioTrack, User user) {
        return new TrackQueueElement(audioTrack, user);
    }

    public static TrackQueueElement empty() {
        return new TrackQueueElement(null, null);
    }

    public boolean isEmpty() {
        return audioTrack == null && user == null;
    }
}
