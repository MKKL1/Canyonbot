package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.queue.SimpleTrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import dev.arbjerg.lavalink.client.Link;
import discord4j.core.object.entity.Guild;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import reactor.core.publisher.Mono;

@Getter
public class LinkContext {
    private final Guild guild;
    private final Link link;
    private final TrackQueue trackQueue;
    private final TrackScheduler trackScheduler;

    public LinkContext(Guild guild, Link link, EventDispatcher eventDispatcher) {
        this.guild = guild;
        this.link = link;
        this.trackQueue = new SimpleTrackQueue();
        this.trackScheduler = new TrackScheduler(trackQueue, link, guild, eventDispatcher);
    }

    //TODO when this method is called, it is not removed from registry
    public Mono<Void> destroy() {
        return link.destroy().then();
    }
}
