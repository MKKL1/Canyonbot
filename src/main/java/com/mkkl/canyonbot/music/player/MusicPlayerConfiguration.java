package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.lavaplayer.LavaPlayer;
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
    public MusicPlayerBaseFactory musicPlayerBaseFactory() {
        return () -> new LavaPlayer(audioPlayerManager.createPlayer());
    }
}
