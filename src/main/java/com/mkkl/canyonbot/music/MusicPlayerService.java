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

    @Bean
    public MusicPlayer musicPlayer(AudioPlayerManager playerManager) {
        return new LavaPlayer(playerManager.createPlayer());
    }

    @Bean
    public AudioPlayerManager audioPlayerManager() {
        System.out.printf("Creating audio player manager%n");
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();//TODO remove search from this audio manager
        AudioSourceManagers.registerRemoteSources(playerManager);
        //playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        return playerManager;
    }
}
