package com.mkkl.canyonbot.musicbot;

import com.mkkl.canyonbot.musicbot.player.MusicPlayer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class MusicBotStarter {
    public static void main(String[] args) {
        var context = SpringApplication.run(MusicBotStarter.class, args);
        context.getBean(MusicPlayer.class).isPaused();
    }
}
