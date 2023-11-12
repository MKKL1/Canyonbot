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

@Getter
public class GuildMusicBotManager {
    private final MusicPlayer player;
    private final Guild guild;
    private final AudioProvider audioProvider;
    private final TrackQueue<TrackQueueElement> trackQueue = new SimpleTrackQueue();
    @Nullable
    private VoiceConnection voiceConnection = null;
    private final TrackScheduler trackScheduler;

    private GuildMusicBotManager(MusicPlayer player, Guild guild, AudioProvider audioProvider) {
        this.player = player;
        this.guild = guild;
        this.audioProvider = audioProvider;
        this.trackScheduler = new TrackScheduler(trackQueue, player);
    }

    public static GuildMusicBotManager create(Guild guild, MusicPlayer musicPlayer, AudioProvider audioProvider) {
        return new GuildMusicBotManager(musicPlayer, guild, audioProvider);
    }

    public Mono<Void> join(VoiceChannel voiceChannel) {
        return voiceChannel.join(AudioChannelJoinSpec.builder().provider(audioProvider).build()).flatMap(vc -> {
            voiceConnection = vc;
            return Mono.empty();
        });
    }

    public Mono<Void> enqueueAndJoin(TrackQueueElement track, VoiceChannel voiceChannel) {
        Mono<Void> mono = Mono.empty();
        if(voiceConnection == null) {
            mono = join(voiceChannel);
        }
        return mono.then(Mono.fromRunnable(() -> trackQueue.enqueue(track)));
    }

    public Mono<Void> enqueue(TrackQueueElement track) {
        if(voiceConnection == null) {
            return Mono.error(new IllegalStateException("Not connected to a voice channel"));
        }
        return Mono.fromRunnable(() -> trackQueue.enqueue(track));
    }

    public Mono<Void> skip() {
        return trackScheduler.skip();
    }

    public Mono<Void> stop() {
        return Mono.empty();//TODO
    }

    public boolean unused() {
        return trackQueue.isEmpty() && player.getPlayingTrack() == null;
    }

    //TODO controlled modification of queue
}
