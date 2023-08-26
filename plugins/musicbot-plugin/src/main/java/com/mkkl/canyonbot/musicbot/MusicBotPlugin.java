package com.mkkl.canyonbot.musicbot;

import com.mkkl.canyonbot.command.BotCommand;
import com.mkkl.canyonbot.command.HelloCommand;
import com.mkkl.canyonbot.plugin.BotPlugin;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.pf4j.PluginWrapper;
import org.reactivestreams.Publisher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MusicBotPlugin extends BotPlugin {

    public MusicBotPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(SpringConfiguration.class);
        applicationContext.refresh();

        return applicationContext;
    }

    @Override
    protected List<BotCommand> getListOfCommands() {
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new HelloCommand());
        return botCommands;
    }

    @Override
    public void start() {
        super.start();
        System.out.println("musicbot-plugin");
    }
}
