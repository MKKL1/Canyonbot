package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.queue.SimpleTrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.player.queue.TrackScheduler;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.AudioChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.AudioChannelJoinSpec;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import jakarta.annotation.Nullable;
import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//TODO right now there are many different methods to interact with the player,
// the best way to solve this is probably to use a command pattern with
// a command queue and a scheduler that executes the commands
//TODO use spring with proper context
public class GuildMusicBotManager {
    private final MusicPlayerBase player;
    private final AudioProvider audioProvider;
    @Getter
    private final Guild guild;
    @Getter
    private final TrackQueue<TrackQueueElement> trackQueue = new SimpleTrackQueue();
    @Nullable
    private VoiceConnection voiceConnection = null;
    @Getter
    private final TrackScheduler trackScheduler;
    @Getter
    private final MusicBotEventDispatcher eventDispatcher;

    private GuildMusicBotManager(MusicPlayerBase player, Guild guild, AudioProvider audioProvider) {
        this.player = player;
        this.guild = guild;
        this.audioProvider = audioProvider;
        this.eventDispatcher = MusicBotEventDispatcher.create(Flux.create(player::registerEvents));
        this.trackScheduler = new TrackScheduler(trackQueue, player, eventDispatcher, this);
    }

    public static GuildMusicBotManager create(Guild guild, MusicPlayerBase musicPlayerBase, AudioProvider audioProvider) {
        return new GuildMusicBotManager(musicPlayerBase, guild, audioProvider);
    }

    //That's not related to music but for now it will stay here
    public Mono<VoiceConnection> join(AudioChannel audioChannel) {
        return audioChannel.join(AudioChannelJoinSpec.builder().provider(audioProvider).build()).flatMap(voiceConnection -> {
            this.voiceConnection = voiceConnection;
            return Mono.just(voiceConnection);
        });
    }

    public Mono<Void> leave() {
        if(!checkConnection()) return Mono.error(new IllegalStateException("Not connected to a voice channel"));
        return voiceConnection.disconnect();
    }

    public boolean unused() {
        return trackQueue.isEmpty() && player.getPlayingTrack() == null;
    }

    public Mono<Boolean> isConnected() {
        if (voiceConnection != null)
            return voiceConnection.isConnected();
        else return Mono.just(false);
    }

    private boolean checkConnection() {
        return voiceConnection != null;
    }

    //TODO controlled modification of queue
}
