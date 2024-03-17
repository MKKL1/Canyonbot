package com.mkkl.canyonbot.music.services;

import com.mkkl.canyonbot.discord.ClientConfiguration;
import com.mkkl.canyonbot.music.exceptions.ChannelNotFoundException;
import com.mkkl.canyonbot.music.exceptions.InvalidAudioChannelException;
import com.mkkl.canyonbot.music.exceptions.MemberNotFoundException;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import com.mkkl.canyonbot.music.player.TrackScheduler;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import dev.arbjerg.lavalink.client.protocol.Track;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceServerUpdateEvent;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.object.entity.channel.AudioChannel;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.spec.AudioChannelJoinSpec;
import discord4j.discordjson.json.gateway.VoiceStateUpdate;
import discord4j.gateway.GatewayClientGroup;
import discord4j.gateway.json.ShardGatewayPayload;
import discord4j.voice.VoiceConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.time.Duration;

@Slf4j
@Service
public class PlayTrackService {
    @Autowired
    private LinkContextRegistry linkContextRegistry;
    @Autowired
    private GatewayDiscordClient gatewayDiscordClient;

    public Mono<Void> playTrack(Guild guild, Mono<Channel> channelMono, Interaction interaction, Track track) {
        return Mono.fromCallable(() -> linkContextRegistry.getOrCreate(guild))
                .flatMap(linkContext -> {
                    Mono<Void> enqueueMono = Mono.fromRunnable(() ->
                            linkContext.getTrackQueue().add(new TrackQueueElement(track, interaction.getUser()))
                    );

                    Mono<Void> initPlayer = linkContext.getLink().getPlayer()
                            .then();

                    Mono<Void> connect = channelMono
                            //Check for audio channel caller is connected to
                            .switchIfEmpty(Mono.justOrEmpty(interaction.getMember())
                                    .switchIfEmpty(Mono.error(new MemberNotFoundException(interaction)))
                                    .flatMap(PartialMember::getVoiceState)
                                    .flatMap(VoiceState::getChannel)
                                    //Check if audio channel was found, else throw exception
                                    .switchIfEmpty(Mono.error(new ChannelNotFoundException(interaction))))
                            .flatMap(channel -> {
                                if (!(channel instanceof AudioChannel))
                                    return Mono.error(new InvalidAudioChannelException(interaction));
                                return Mono.just((AudioChannel) channel);
                            })
                            .flatMap(audioChannel -> audioChannel.sendConnectVoiceState(false, false));

                    Mono<Void> joinMono = gatewayDiscordClient.getVoiceConnectionRegistry().getVoiceConnection(guild.getId())
                            .switchIfEmpty(connect.cast(VoiceConnection.class))//TODO test
                            .then();


                    Mono<Void> startMono = Mono.defer(() -> {
                        if (linkContext.getTrackScheduler().getState() != TrackScheduler.State.PLAYING)
                            return linkContext.getTrackScheduler().beginPlayback();
                        return Mono.empty();
                    });
                    return enqueueMono.and(initPlayer).and(joinMono).then(startMono);
                });
    }
}
