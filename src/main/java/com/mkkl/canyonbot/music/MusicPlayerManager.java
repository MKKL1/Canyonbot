package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.music.player.GuildMusicBotManager;
import com.mkkl.canyonbot.music.player.lavaplayer.LavaPlayer;
import com.mkkl.canyonbot.music.player.MusicPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
            MusicPlayer musicPlayer = new LavaPlayer(audioPlayerManager.createPlayer());
            return GuildMusicBotManager.create(_guild, musicPlayer, musicPlayer.getAudioProvider());
        });
    }

    //TODO optional?
    public GuildMusicBotManager getPlayer(Guild guild) {
        return playerPool.get(guild);
    }
}
