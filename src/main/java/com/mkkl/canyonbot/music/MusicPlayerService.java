package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.music.player.LavaPlayer;
import com.mkkl.canyonbot.music.player.LavaPlayerAudioProvider;
import com.mkkl.canyonbot.music.player.MusicPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import discord4j.voice.AudioProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class MusicPlayerService {

    //TODO there should be separate MusicPlayer for each guild
    @Bean
    public MusicPlayer musicPlayer(AudioPlayer audioPlayer) {
        return new LavaPlayer(audioPlayer);
    }

    //TODO everything below should be internal
    @Bean
    public AudioPlayer audioPlayer(AudioPlayerManager playerManager) {
        return playerManager.createPlayer();
    }

    @Bean
    public AudioPlayerManager audioPlayerManager() {
        System.out.printf("Creating audio player manager%n");
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();//TODO remove search from this audio manager
        //playerManager.getConfiguration().setFrameBufferFactory(new );
        AudioSourceManagers.registerRemoteSources(playerManager);

        return playerManager;
    }

    @Bean
    public AudioProvider audioProvider(AudioPlayer audioPlayer) {
        return new LavaPlayerAudioProvider(audioPlayer);
    }
}
