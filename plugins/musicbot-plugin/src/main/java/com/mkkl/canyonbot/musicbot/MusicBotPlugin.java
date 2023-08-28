package com.mkkl.canyonbot.musicbot;

import com.mkkl.canyonbot.plugin.api.AppPluginContext;
import com.mkkl.canyonbot.plugin.api.BotPlugin;
import com.mkkl.canyonbot.plugin.api.PluginContext;
import com.mkkl.canyonbot.command.BotCommand;
import com.mkkl.canyonbot.command.CommandList;
import discord4j.common.util.Snowflake;

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
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        //Unregister commands
    }
}
