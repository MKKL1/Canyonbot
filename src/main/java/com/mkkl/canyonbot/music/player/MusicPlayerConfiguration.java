package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.lavaplayer.LavaPlayerService;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MusicPlayerConfiguration {
    private final AudioPlayerManager audioPlayerManager;

    public MusicPlayerConfiguration() {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
    }

    @Bean
    public MusicPlayerBaseService musicPlayerBaseService() {
        return new LavaPlayerService(audioPlayerManager.createPlayer());
    }
}
