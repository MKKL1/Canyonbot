package com.mkkl.canyonbot.music.services;

import com.mkkl.canyonbot.music.VoiceConnectionRegistry;
import com.mkkl.canyonbot.music.exceptions.LinkContextNotFound;
import com.mkkl.canyonbot.music.player.LinkContext;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import com.mkkl.canyonbot.music.player.TrackScheduler;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.player.queue.TrackQueueInfo;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.libraries.discord4j.Discord4JUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.AudioChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

//TODO cleanup of player on correct event
@Slf4j
@Service
public class PlayerService {
    private final LinkContextRegistry linkContextRegistry;
    private final VoiceConnectionRegistry voiceConnectionRegistry;
    private final GatewayDiscordClient gatewayDiscordClient;

    public PlayerService(LinkContextRegistry linkContextRegistry, VoiceConnectionRegistry voiceConnectionRegistry, GatewayDiscordClient gatewayDiscordClient) {
        this.linkContextRegistry = linkContextRegistry;
        this.voiceConnectionRegistry = voiceConnectionRegistry;
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

    public Mono<Void> beginPlayback(long guildId) {
        return Mono.fromCallable(() -> linkContextRegistry.getOrCreate(guildId))
                .filter(linkContext -> linkContext.getTrackScheduler().getState() != TrackScheduler.State.PLAYING)
                .flatMap(linkContext -> linkContext.getTrackScheduler().beginPlayback());
    }

    public Mono<Void> stopPlayback(long guildId) {
        return Mono.justOrEmpty(linkContextRegistry.getCached(guildId))
                .switchIfEmpty(Mono.error(new LinkContextNotFound()))
                .filter(linkContext -> linkContext.getTrackScheduler().getState() != TrackScheduler.State.STOPPED)
                .flatMap(linkContext -> linkContext.getTrackScheduler().stop());
    }

    public Mono<Void> pausePlayback(long guildId) {
        return Mono.justOrEmpty(linkContextRegistry.getCached(guildId))
                .switchIfEmpty(Mono.error(new LinkContextNotFound()))
                .filter(linkContext -> linkContext.getTrackScheduler().getState() != TrackScheduler.State.PAUSED)
                .flatMap(linkContext -> linkContext.getTrackScheduler().pause());
    }

    //TODO move it to queue service?
    public Mono<TrackQueueElement> skipTrack(long guildId) {
        return Mono.justOrEmpty(linkContextRegistry.getCached(guildId))
                .switchIfEmpty(Mono.error(new LinkContextNotFound()))
                .flatMap(linkContext -> linkContext.getTrackScheduler().skip());
    }

    public boolean addTrackToQueue(long guildId, Track track, User user) {
        LinkContext linkContext = linkContextRegistry.getOrCreate(guildId);
        return linkContext.getTrackQueue().add(new TrackQueueElement(track, user));
    }

    public boolean addTracksToQueue(long guildId, Collection<TrackQueueElement> trackQueueElements) {
        LinkContext linkContext = linkContextRegistry.getOrCreate(guildId);
        return linkContext.getTrackQueue().addAll(trackQueueElements);
    }

    public void clearQueue(long guildId) {
        Optional<LinkContext> cached = linkContextRegistry.getCached(guildId);
        if(cached.isEmpty()) throw new LinkContextNotFound();
        cached.get().getTrackQueue().clear();
    }

    public void shuffleQueue(long guildId) {
        Optional<LinkContext> cached = linkContextRegistry.getCached(guildId);
        if(cached.isEmpty()) throw new LinkContextNotFound();
        cached.get().getTrackQueue().shuffle();
    }

    //Not sure if it is good idea to return track queue instance
    public TrackQueueInfo getTrackQueueInfo(long guildId) {
        Optional<LinkContext> cached = linkContextRegistry.getCached(guildId);
        if(cached.isEmpty()) throw new LinkContextNotFound();
        LinkContext linkContext = cached.get();
        List<TrackQueueElement> trackList = linkContext.getTrackQueue()
                .stream()
                .toList();
        return new TrackQueueInfo(trackList, linkContext.getTrackScheduler()
                .getCurrentTrack(), linkContext.getTrackScheduler().getState());
    }

    public Mono<Void> join(AudioChannel audioChannel) {
        return Mono.fromCallable(() -> voiceConnectionRegistry.isSet(audioChannel.getGuildId().asLong())) //TODO check if we can skip sending voicestate if we are already connected
                    .filter(isSet -> !isSet)
                    .flatMap(ignore -> audioChannel.sendConnectVoiceState(false, false));
    }

    public Mono<Void> leaveChannel(Snowflake guildId) {
        return Discord4JUtils.leave(gatewayDiscordClient, guildId);
    }

    //it is done automatically when bot disconnects from channel
    public Mono<Void> destroyLinkContext(long guildId) {
        return linkContextRegistry.destroy(guildId).then(Mono.fromRunnable(() -> log.info("Destroyed link context")));
    }

}
