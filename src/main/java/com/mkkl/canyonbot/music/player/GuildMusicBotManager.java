package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.event.scheduler.QueueEmptyEvent;
import com.mkkl.canyonbot.music.player.queue.SimpleTrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.player.queue.TrackSchedulerData;
import discord4j.core.object.entity.Guild;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import jakarta.annotation.Nullable;
import lombok.Getter;

//TODO right now there are many different methods to interact with the player,
// the best way to solve this is probably to use a command pattern with
// a command queue and a scheduler that executes the commands
//TODO use spring with proper context
public class GuildMusicBotManager {
    private final MusicPlayerBaseService player;
    private final AudioProvider audioProvider;
    @Getter
    private final Guild guild;
    @Getter
    private final TrackQueue<TrackQueueElement> trackQueue = new SimpleTrackQueue();
    @Nullable
    private VoiceConnection voiceConnection = null;
    @Getter
    private final TrackSchedulerData trackScheduler;
    @Getter
    private final MusicBotEventDispatcher eventDispatcher;

    private GuildMusicBotManager(MusicPlayerBaseService player, Guild guild, AudioProvider audioProvider) {
        this.player = player;
        this.guild = guild;
        this.audioProvider = audioProvider;
        this.eventDispatcher = MusicBotEventDispatcher.create();
        this.player.registerEvents(this, eventDispatcher);
        this.trackScheduler = new TrackSchedulerData(trackQueue, player, eventDispatcher, this);
        //TODO this should be configurable
        eventDispatcher.on(QueueEmptyEvent.class).flatMap(queueEmptyEvent -> leave()).subscribe();
    }

    public static GuildMusicBotManager create(Guild guild, MusicPlayerBaseService musicPlayerBaseService, AudioProvider audioProvider) {
        return new GuildMusicBotManager(musicPlayerBaseService, guild, audioProvider);
    }
}
