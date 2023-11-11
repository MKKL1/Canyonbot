package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.queue.VoiceConnectionQueueManager;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
