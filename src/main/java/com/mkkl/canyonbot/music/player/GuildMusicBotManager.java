package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.queue.SimpleTrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import com.mkkl.canyonbot.music.player.queue.TrackScheduler;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.AudioChannelJoinSpec;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import jakarta.annotation.Nullable;
import lombok.Getter;
import reactor.core.publisher.Mono;


public class GuildMusicBotManager {
    private final MusicPlayerBase player;
    @Getter
    private final Guild guild;
    private final AudioProvider audioProvider;
    private final TrackQueue<TrackQueueElement> trackQueue = new SimpleTrackQueue();
    @Nullable
    private VoiceConnection voiceConnection = null;
    @Getter
    private final TrackScheduler trackScheduler;

    private GuildMusicBotManager(MusicPlayerBase player, Guild guild, AudioProvider audioProvider) {
        this.player = player;
        this.guild = guild;
        this.audioProvider = audioProvider;
        this.trackScheduler = new TrackScheduler(trackQueue, player);
    }

    public static GuildMusicBotManager create(Guild guild, MusicPlayerBase musicPlayerBase, AudioProvider audioProvider) {
        return new GuildMusicBotManager(musicPlayerBase, guild, audioProvider);
    }

    //That's not related to music but for now it will stay here
    public Mono<VoiceConnection> join(VoiceChannel voiceChannel) {
        return voiceChannel.join(AudioChannelJoinSpec.builder().provider(audioProvider).build()).flatMap(voiceConnection -> {
            this.voiceConnection = voiceConnection;
            return Mono.just(voiceConnection);
        });
    }

    public Mono<Void> leave() {
        if(!checkConnection()) return Mono.error(new IllegalStateException("Not connected to a voice channel"));
        return voiceConnection.disconnect();
    }

    //TODO this should be moved to TrackScheduler
//    public Mono<Void> playInstant(TrackQueueElement track) {
//        if(!checkConnection()) return Mono.error(new IllegalStateException("Not connected to a voice channel"));
//        return Mono.fromRunnable(() -> {
//            trackQueue.enqueue(track);
//            if(player.getPlayingTrack() == null) {
//                trackScheduler.nextTrack();
//            }
//        });
//    }
//    public Mono<Void> skip() {
//        return trackScheduler.skip();
//    }
//    public Mono<Void> stop() {
//        return Mono.empty();//TODO
//    }

    public boolean unused() {
        return trackQueue.isEmpty() && player.getPlayingTrack() == null;
    }

    private boolean checkConnection() {
        return voiceConnection != null;
    }

    //TODO controlled modification of queue
}
