package com.mkkl.canyonbot.music.player.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//TODO change name to something more descriptive
@Getter
@AllArgsConstructor
public class TrackQueueElement {
    private final AudioTrack audioTrack;
    private final User user;

    public static TrackQueueElement of(AudioTrack audioTrack, User user) {
        return new TrackQueueElement(audioTrack, user);
    }

    public static List<TrackQueueElement> listOf(Collection<AudioTrack> audioTracks, User user) {
        List<TrackQueueElement> list = new ArrayList<>(audioTracks.size());
        for (AudioTrack track : audioTracks) {
            list.add(TrackQueueElement.of(track, user));
        }
        return list;
    }
}
