package com.mkkl.canyonbot.music.services;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import com.mkkl.canyonbot.music.player.TrackScheduler;
import com.mkkl.canyonbot.music.player.event.GuildPlayerCreationEvent;
import com.mkkl.canyonbot.music.player.event.base.TrackEndEvent;
import com.mkkl.canyonbot.music.player.event.scheduler.QueueEmptyEvent;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.player.queue.TrackSchedulerData;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import discord4j.core.object.entity.Guild;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private GuildTrackQueueService guildTrackQueueService;
    private final Map<Guild, TrackScheduler> trackSchedulerDataMap = new ConcurrentHashMap<>();

    @EventListener
    private void handleGuildPlayerCreation(GuildPlayerCreationEvent event) {
        GuildMusicBot guildMusicBot = event.getGuildMusicBot();
        if(trackSchedulerDataMap.containsKey(guildMusicBot.getGuild()))
            return;

        TrackScheduler trackScheduler = new TrackScheduler(guildMusicBot);
        trackSchedulerDataMap.put(guildMusicBot.getGuild(), trackScheduler);


        guildMusicBot.getEventDispatcher().on(TrackEndEvent.class)
            .doOnNext(trackEndEvent -> {
                if (trackEndEvent.getEndReason() != AudioTrackEndReason.CLEANUP) {
                    TrackQueueElement track = guildMusicBot.getTrackQueue().dequeue();
                    trackScheduler.setCurrentTrack(track);
                    if (track != null) {
                        guildMusicBot.getPlayer().playTrack(track.getAudioTrack());
                    } else {
                        guildMusicBot.getEventDispatcher().publish(new QueueEmptyEvent(guildMusicBot.getGuild(), guildMusicBot.getTrackQueue()));
                        trackScheduler.setState(TrackScheduler.State.STOPPED);
                    }
                } else {
                    trackScheduler.setState(TrackScheduler.State.STOPPED);
                    trackScheduler.setCurrentTrack(null);
                    //Cleanup
                    //return guildMusicBotManager.leave();//TODO event here as well
                }
            })
                .subscribe();
        log.info("Created track scheduler for guild " + guildMusicBot.getGuild().getName());
    }

    public void startPlaying(Guild guild) { //Maybe we should use something like GuildSession to easily manage cleanup
        TrackScheduler trackScheduler = getOrThrow(guild);
        if (trackScheduler.getState() != TrackScheduler.State.STOPPED)
            return;
//            throw new IllegalStateException("Already playing"); //Not sure if I should throw error or simply ignore
        TrackQueueElement trackQueueElement = trackScheduler.getGuildMusicBot().getTrackQueue().dequeue();
        if(trackQueueElement == null) throw new IllegalStateException("Queue is empty");
        trackScheduler.setCurrentTrack(trackQueueElement);
        trackScheduler.getGuildMusicBot().getPlayer().playTrack(trackQueueElement.getAudioTrack());
        trackScheduler.setState(TrackScheduler.State.PLAYING);
    }

    public void stopPlaying(Guild guild) {
        TrackScheduler trackScheduler = getOrThrow(guild);
        if (trackScheduler.getState() == TrackScheduler.State.STOPPED)
            return;
//            throw new IllegalStateException("Already stopped");
        trackScheduler.getGuildMusicBot().getTrackQueue().clear();
        trackScheduler.getGuildMusicBot().getPlayer().stopTrack();
        trackScheduler.setCurrentTrack(null);
        trackScheduler.setState(TrackScheduler.State.STOPPED);
        //TODO stop event
    }

    public Mono<Void> playInstant(Guild guild, TrackQueueElement trackQueueElement) {
        return Mono.empty();
    }

    public Mono<Void> replaceCurrent(Guild guild, TrackQueueElement trackQueueElement) {
        return Mono.empty();
    }

    public Optional<TrackQueueElement> skip(Guild guild) {
        TrackScheduler trackScheduler = getOrThrow(guild);
        if (trackScheduler.getState() == TrackScheduler.State.STOPPED || trackScheduler.getCurrentTrack() == null)
            return Optional.empty();
//            return Mono.error(new IllegalStateException("Nothing to skip"));
        //TODO skip event
        TrackQueueElement trackQueueElement = guildTrackQueueService.dequeue(guild);
        trackScheduler.getGuildMusicBot().getPlayer().stopTrack();
        return Optional.ofNullable(trackQueueElement);
    }

    //TODO maybe we can create TrackSchedulerRepository to manage TrackScheduler instances
    public Optional<TrackQueueElement> getCurrentTrack(Guild guild) {
        return Optional.ofNullable(getOrThrow(guild).getCurrentTrack());
    }

    public TrackScheduler.State getState(Guild guild) {
        return getOrThrow(guild).getState();
    }


    private TrackScheduler getOrThrow(Guild guild) {
        return getTrackSchedulerData(guild).orElseThrow(() -> new IllegalStateException("No scheduler for guild"));
    }

    public boolean isPresent(Guild guild) {
        return trackSchedulerDataMap.containsKey(guild);
    }

    private Optional<TrackScheduler> getTrackSchedulerData(Guild guild) {
        return Optional.ofNullable(trackSchedulerDataMap.get(guild));
    }


}
