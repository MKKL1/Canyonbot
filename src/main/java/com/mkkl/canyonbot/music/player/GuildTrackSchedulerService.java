package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.event.GuildPlayerCreationEvent;
import com.mkkl.canyonbot.music.player.event.base.TrackEndEvent;
import com.mkkl.canyonbot.music.player.event.scheduler.QueueEmptyEvent;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.player.queue.TrackSchedulerData;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import discord4j.core.object.entity.Guild;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class GuildTrackSchedulerService {
    private final Map<Guild, TrackScheduler> trackSchedulerDataMap = new ConcurrentHashMap<>();

    @EventListener
    private void handleGuildPlayerCreation(GuildPlayerCreationEvent event) {
        GuildMusicBot guildMusicBot = event.getGuildMusicBot();
        if(trackSchedulerDataMap.containsKey(guildMusicBot.getGuild()))
            return;
        TrackScheduler trackScheduler = new TrackScheduler(guildMusicBot);
        trackSchedulerDataMap.put(guildMusicBot.getGuild(), trackScheduler);
        guildMusicBot.getEventDispatcher().on(TrackEndEvent.class)
                .flatMap(trackEndEvent -> {
                    if (trackEndEvent.getEndReason() != AudioTrackEndReason.CLEANUP) {
                        return Mono.fromRunnable(() -> {
                            TrackQueueElement track = guildMusicBot.getTrackQueue().dequeue();
                            trackScheduler.setCurrentTrack(track);
                            if (track.isEmpty()) {
                                guildMusicBot.getPlayer().playTrack(track.getAudioTrack());
                            } else {
                                guildMusicBot.getEventDispatcher().publish(new QueueEmptyEvent(guildMusicBot, guildMusicBot.getTrackQueue()));
                                trackScheduler.setState(TrackScheduler.State.STOPPED);
                            }
                        });
                    }
                    trackScheduler.setState(TrackScheduler.State.STOPPED);
                    trackScheduler.setCurrentTrack(TrackQueueElement.empty());
                    //Cleanup
                    //return guildMusicBotManager.leave();//TODO event here as well
                    return Mono.empty();//TODO !!!leave
                })
                .subscribe();
        log.info("Created track scheduler for guild " + guildMusicBot.getGuild().getName());
    }

    public void startPlaying(Guild guild) { //Maybe we should use something like GuildSession to easily manage cleanup
        //TODO remove checks outside of mono
        TrackScheduler trackScheduler = getOrThrow(guild);
        if (trackScheduler.getState() != TrackScheduler.State.STOPPED)
            throw new IllegalStateException("Already playing");
        TrackQueueElement trackQueueElement = trackScheduler.getGuildMusicBot().getTrackQueue().dequeue();
        if(trackQueueElement == null) throw new IllegalStateException("Queue is empty");
        //TODO not sure if using setters with object is a good idea, it means this object is mutable, therefore it cannot be safely passed around
        trackScheduler.setCurrentTrack(trackQueueElement);
        trackScheduler.getGuildMusicBot().getPlayer().playTrack(trackQueueElement.getAudioTrack());
        trackScheduler.setState(TrackScheduler.State.PLAYING);
    }

    //TODO it doesn't really need to be mono
    public Mono<Void> stopPlaying(Guild guild) {
        TrackScheduler trackScheduler = getOrThrow(guild);
        if (trackScheduler.getState() == TrackScheduler.State.STOPPED)
            return Mono.error(new IllegalStateException("Already stopped"));
        return Mono.fromRunnable(() -> {
            trackScheduler.getGuildMusicBot().getTrackQueue().clear();
            trackScheduler.getGuildMusicBot().getPlayer().stopTrack();
            //TODO The longer I look at this, the more I think that TrackQueue should be non-generic
            trackScheduler.setCurrentTrack(TrackQueueElement.empty());
            trackScheduler.setState(TrackScheduler.State.STOPPED);
            //TODO stop event
        });
    }

    public Mono<Void> playInstant(Guild guild, TrackQueueElement trackQueueElement) {
        return Mono.empty();
    }

    public Mono<Void> replaceCurrent(Guild guild, TrackQueueElement trackQueueElement) {
        return Mono.empty();
    }

    public Mono<TrackQueueElement> skip(Guild guild) {
        TrackScheduler trackScheduler = getOrThrow(guild);
        if (trackScheduler.getState() == TrackScheduler.State.STOPPED || trackScheduler.getCurrentTrack().isEmpty())
            return Mono.error(new IllegalStateException("Nothing to skip"));
        //TODO skip event
        return Mono.just(Objects.requireNonNull(trackScheduler.getCurrentTrack()))
                .then(Mono.fromRunnable(() -> trackScheduler.getGuildMusicBot().getPlayer().stopTrack()));
    }

    //TODO maybe we can create TrackSchedulerRepository to manage TrackScheduler instances
    //TODO Ask someone if this is good idea
    public TrackQueueElement getCurrentTrack(Guild guild) {
        return getOrThrow(guild).getCurrentTrack();
    }

    public TrackScheduler.State getState(Guild guild) {
        return getOrThrow(guild).getState();
    }

    private TrackScheduler getOrThrow(Guild guild) {
        return getTrackSchedulerData(guild).orElseThrow(() -> new IllegalStateException("No scheduler for guild"));
    }

    private Optional<TrackScheduler> getTrackSchedulerData(Guild guild) {
        return Optional.ofNullable(trackSchedulerDataMap.get(guild));
    }


}
