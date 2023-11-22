package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.lavaplayer.LavaPlayer;
import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MusicPlayerManager {
    private final Map<Guild, GuildMusicBotManager> playerPool = new HashMap<>();
    private final AudioPlayerManager audioPlayerManager;
    public MusicPlayerManager() {
        audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    }

    public GuildMusicBotManager getOrCreatePlayer(Guild guild) {
        return playerPool.computeIfAbsent(guild, _guild -> {
            MusicPlayerBase musicPlayerBase = new LavaPlayer(audioPlayerManager.createPlayer());
            return GuildMusicBotManager.create(_guild, musicPlayerBase, musicPlayerBase.createAudioProvider());
        });
    }

    public Optional<GuildMusicBotManager> getPlayer(Guild guild) {
        return Optional.ofNullable(playerPool.get(guild));
    }
}
