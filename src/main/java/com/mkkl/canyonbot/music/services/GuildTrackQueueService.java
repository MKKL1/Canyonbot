package com.mkkl.canyonbot.music.services;

import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.object.entity.Guild;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class GuildTrackQueueService {
    private final Map<Guild, TrackQueue> guildTrackQueueMap = new ConcurrentHashMap<>();
    @EventListener
    private void handleGuildPlayerCreation(GuildPlayerCreationEvent event) {
        Guild guild = event.getLinkContext().getGuild();
        guildTrackQueueMap.put(guild, event.getLinkContext().getTrackQueue());
        log.debug("Created TrackQueue for guild " + guild);
    }

    public void add(Guild guild, TrackQueueElement trackQueueElement) {
        getTrackQueue(guild).add(trackQueueElement);
    }

    public void addAll(Guild guild, Collection<? extends TrackQueueElement> tracks) {
        getTrackQueue(guild).addAll(tracks);
    }

    public TrackQueueElement dequeue(Guild guild) {
        return getTrackQueue(guild).dequeue();
    }

    public int size(Guild guild) {
        return getTrackQueue(guild).size();
    }

    public Iterator<TrackQueueElement> iterator(Guild guild) {
        return getTrackQueue(guild).iterator();
    }

    public boolean isPresent(Guild guild) {
        return guildTrackQueueMap.containsKey(guild);
    }

    private @NonNull TrackQueue getTrackQueue(Guild guild) {
        TrackQueue trackQueue = guildTrackQueueMap.get(guild);
        if(trackQueue == null) throw new IllegalStateException("Track queue for guild " + guild.getName() + " has not been initialized");
        return trackQueue;
    }
}
