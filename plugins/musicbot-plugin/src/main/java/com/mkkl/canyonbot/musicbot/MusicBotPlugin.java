package com.mkkl.canyonbot.musicbot;

import com.mkkl.canyonbot.CanyonBot;
import com.mkkl.canyonbot.musicbot.commands.HelloCommand;
import com.mkkl.canyonbot.musicbot.player.MusicPlayer;
import com.mkkl.canyonbot.plugin.api.AppPluginContext;
import com.mkkl.canyonbot.plugin.api.BotPlugin;
import com.mkkl.canyonbot.plugin.api.PluginContext;
import com.mkkl.canyonbot.command.BotCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import discord4j.core.DiscordClient;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



public class MusicBotPlugin extends BotPlugin {

    public MusicBotPlugin(PluginContext pluginContext, AppPluginContext appPluginContext) {
        super(pluginContext, appPluginContext);
    }

    @Override
    public Optional<List<BotCommand>> getCommands() {
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new HelloCommand());
        return Optional.of(botCommands);
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        SpringApplication springApplication = new SpringApplication(MusicBotStarter.class);
        //        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
//        applicationContext.registerBean(CanyonBot.class, appPluginContext::getCanyonBot);
//        applicationContext.register(SpringConfiguration.class);
//        applicationContext.refresh();
        var context = springApplication.run();
        context.getBeanFactory().registerSingleton("pluginContext", pluginContext);
        context.getBeanFactory().registerSingleton("appPluginContext", appPluginContext);
        context.refresh();
        return context;
    }

    @Override
    public void start() {
        super.start();
        System.out.println("MusicBotPlugin started " + applicationContext.getBean(MusicPlayer.class).isPaused());

    }

    @Override
    public void stop() {
        super.stop();
        //Unregister commands
    }
}
