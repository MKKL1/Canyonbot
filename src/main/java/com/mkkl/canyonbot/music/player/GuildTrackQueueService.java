package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.queue.SimpleTrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.player.queue.TrackSchedulerData;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GuildTrackQueueService {
    private final Map<Guild, TrackQueue<TrackQueueElement>> guildTrackQueueMap = new ConcurrentHashMap<>();

    public TrackQueue<TrackQueueElement> getTrackQueue(Guild guild) {
        return guildTrackQueueMap.computeIfAbsent(guild, guild1 -> new SimpleTrackQueue());
    }

}
