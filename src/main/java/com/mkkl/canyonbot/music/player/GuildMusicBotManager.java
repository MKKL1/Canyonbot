package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.event.MusicPlayerEvent;
import com.mkkl.canyonbot.music.player.event.PlayerPauseEvent;
import com.mkkl.canyonbot.music.player.queue.VoiceConnectionQueueManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public class GuildMusicBotManager {
    private final MusicPlayer player;
    private final Guild guild;
    private final VoiceConnectionQueueManager queueManager;

    public static GuildMusicBotManager create(Guild guild, MusicPlayer musicPlayer, AudioProvider audioProvider) {
        return new GuildMusicBotManager(musicPlayer, guild, new VoiceConnectionQueueManager(audioProvider));
    }

    public void join(VoiceChannel voiceChannel) {

    }

    public void play() {

    }
}
