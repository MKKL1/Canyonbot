package com.mkkl.canyonbot.music.services;

import com.mkkl.canyonbot.discord.GuildVoiceConnectionService;
import com.mkkl.canyonbot.music.exceptions.ChannelNotFoundException;
import com.mkkl.canyonbot.music.exceptions.InvalidAudioChannelException;
import com.mkkl.canyonbot.music.exceptions.MemberNotFoundException;
import com.mkkl.canyonbot.music.player.TrackScheduler;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import dev.arbjerg.lavalink.client.protocol.Track;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.object.entity.channel.AudioChannel;
import discord4j.core.object.entity.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PlayTrackService {
    @Autowired
    private GuildMusicBotService guildMusicBotService;
    @Autowired
    private GuildVoiceConnectionService guildVoiceConnectionService;
    @Autowired
    private GuildTrackSchedulerService guildTrackSchedulerService;

    public Mono<Void> playTrack(Guild guild, Mono<Channel> channelMono, Interaction interaction, Track track) {
        return Mono.just(guildMusicBotService.getGuildMusicBot(guild)
                        .orElse(guildMusicBotService.createGuildMusicBot(guild)))
                .flatMap(guildMusicBot -> {
                    Mono<Void> enqueueMono = Mono.fromRunnable(() -> guildMusicBot.getTrackQueue()
                            .add(new TrackQueueElement(track, interaction.getUser())));

                    Mono<Void> joinMono = guildVoiceConnectionService.isConnected(guild)
                            .filter(isConnected -> !isConnected)
                            .flatMap(ignore -> channelMono
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
                                    .flatMap(audioChannel -> guildVoiceConnectionService.join(guild, guildMusicBot.getPlayer()
                                            .getAudioProvider(), audioChannel)))
                            .then();


                    Mono<Void> startMono = Mono.fromRunnable(() -> {
                        if (guildTrackSchedulerService.getState(guild) == TrackScheduler.State.STOPPED)
                            guildTrackSchedulerService.startPlaying(guild);
                    });
                    return enqueueMono.and(joinMono).then(startMono);
                });
    }
}
