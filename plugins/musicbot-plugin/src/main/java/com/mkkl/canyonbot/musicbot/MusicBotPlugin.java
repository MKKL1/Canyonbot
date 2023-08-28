package com.mkkl.canyonbot.musicbot;

import com.mkkl.canyonbot.plugin.api.AppPluginContext;
import com.mkkl.canyonbot.plugin.api.BotPlugin;
import com.mkkl.canyonbot.plugin.api.PluginContext;
import com.mkkl.canyonbot.command.BotCommand;
import com.mkkl.canyonbot.command.CommandList;
import discord4j.common.util.Snowflake;

import java.util.ArrayList;
import java.util.List;

public class MusicBotPlugin extends BotPlugin {


    public MusicBotPlugin(PluginContext pluginContext, AppPluginContext appPluginContext) {
        super(pluginContext, appPluginContext);
    }

    @Override
    public void start() {
        super.start();
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new HelloCommand());
        appPluginContext.getCanyonBot().getCommandRegistry().AddCommands(
                appPluginContext.getCanyonBot().getDiscordClient(),
                new CommandList("musicbot-plugin", commandList))
            .registerCommands(Snowflake.of(1143642302435315732L)).subscribe();
    }

    @Override
    public void stop() {
        super.stop();
        //Unregister commands
    }
}
