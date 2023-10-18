package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.music.player.LavaPlayer;
import com.mkkl.canyonbot.music.player.MusicPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class MusicPlayerService {

    private AudioPlayerManager playerManager;
    @Bean
    public MusicPlayer musicPlayer() {
        return new LavaPlayer(playerManager.createPlayer());
    }

    @Bean
    public AudioPlayerManager audioPlayerManager() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        //playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        return playerManager;
    }
}
