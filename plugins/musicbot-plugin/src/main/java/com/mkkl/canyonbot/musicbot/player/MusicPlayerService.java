package com.mkkl.canyonbot.musicbot.player;

import com.mkkl.canyonbot.CanyonBot;
import com.mkkl.canyonbot.plugin.api.AppPluginContext;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class MusicPlayerService {

    @Bean
    public MusicPlayer musicPlayer() {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        return new LavaPlayer(playerManager.createPlayer());
    }
}
