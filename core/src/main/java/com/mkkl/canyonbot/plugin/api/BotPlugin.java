package com.mkkl.canyonbot.plugin.api;

import com.mkkl.canyonbot.command.BotCommand;
import com.mkkl.canyonbot.command.CommandList;
import org.pf4j.Plugin;

import java.util.List;
import java.util.Optional;

public abstract class BotPlugin extends Plugin {
    protected final AppPluginContext appPluginContext;
    protected final PluginContext pluginContext;
    protected BotPlugin(PluginContext pluginContext, AppPluginContext appPluginContext) {
        super();
        this.pluginContext = pluginContext;
        this.appPluginContext = appPluginContext;
    }

    public Optional<List<BotCommand>> getCommands() {
        return Optional.empty();
    }

    @Override
    public void start() {
        super.start();
        Optional<List<BotCommand>> commands = getCommands();
        commands.ifPresent(botCommands -> appPluginContext.getCanyonBot()
                .getCommandRegistry()
                .AddCommands(new CommandList(pluginContext.pluginDescriptor().getPluginId(), botCommands)));
    }

    @Override
    public void stop() {
        super.stop();
        //Not removing commands for now
//        Optional<List<BotCommand>> commands = getCommands();
//        commands.ifPresent(botCommands -> appPluginContext.getCanyonBot()
//                .getCommandRegistry()
//                .AddCommands(new CommandList(pluginContext.pluginDescriptor().getPluginId(), botCommands)));
    }
}
