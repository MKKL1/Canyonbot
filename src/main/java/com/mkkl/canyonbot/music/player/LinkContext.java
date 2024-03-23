package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.queue.SimpleTrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import dev.arbjerg.lavalink.client.Link;
import lombok.Getter;
import reactor.core.publisher.Mono;

@Getter
public class LinkContext {
    private final long guildId;
    private final Link link;
    private final TrackQueue trackQueue;
    private final TrackScheduler trackScheduler;

    public LinkContext(long guildId, Link link, EventDispatcher eventDispatcher) {
        this.guildId = guildId;
        this.link = link;
        this.trackQueue = new SimpleTrackQueue();
        this.trackScheduler = new TrackScheduler(trackQueue, link, guildId, eventDispatcher);
    }

    //TODO when this method is called, it is not removed from registry
    public Mono<Void> destroy() {
        return link.destroy().then();
    }
}
