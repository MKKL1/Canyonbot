package com.mkkl.canyonbot.music.player.queue;

import dev.arbjerg.lavalink.client.player.Track;
import discord4j.core.object.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//TODO change name to something more descriptive
@Getter
public class TrackQueueElement {
    private final Track track;
    private final User user;
    private final boolean hide;

    public TrackQueueElement(Track track, User user) {
        this.track = track;
        this.user = user;
        this.hide = false;
    }

    public TrackQueueElement(Track track, User user, boolean hide) {
        this.track = track;
        this.user = user;
        this.hide = hide;
    }

    public String getTitle() {
        return hide ? " " : track.getInfo().getTitle();
    }

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
