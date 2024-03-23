package com.mkkl.canyonbot.music.player.queue;

import dev.arbjerg.lavalink.client.protocol.Track;
import discord4j.core.object.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//TODO change name to something more descriptive
@Getter
@AllArgsConstructor
public class TrackQueueElement {
    private final Track track;
    private final User user;

    public static TrackQueueElement of(Track track, User user) {
        return new TrackQueueElement(track, user);
    }

    public static List<TrackQueueElement> listOf(Collection<Track> tracks, User user) {
        List<TrackQueueElement> list = new ArrayList<>(tracks.size());
        for (Track trackElement : tracks) {
            list.add(TrackQueueElement.of(trackElement, user));
        }
        return list;
    }
}
