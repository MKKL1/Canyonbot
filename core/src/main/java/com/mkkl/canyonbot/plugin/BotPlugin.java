package com.mkkl.canyonbot.plugin;

import com.mkkl.canyonbot.CanyonBot;
import com.mkkl.canyonbot.command.BotCommand;
import com.mkkl.canyonbot.command.CommandList;
import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.pf4j.spring.SpringPluginManager;

import java.util.ArrayList;
import java.util.List;

public abstract class BotPlugin extends SpringPlugin {
    protected CanyonBot canyonBot;//Cannot really autowire
    public BotPlugin(PluginWrapper wrapper) {
        super(wrapper);
        canyonBot = ((SpringPluginManager)wrapper.getPluginManager()).getApplicationContext().getBean(CanyonBot.class);
    }

    protected List<BotCommand> getListOfCommands() {
        return null;
    }

    @Extension
    public static class BotCommandPovider implements CommandProviderExtension {
        final List<BotCommand> commandList;

        public BotCommandPovider(List<BotCommand> commandList) {
            this.commandList = commandList;
        }

        @Override
        public CommandList getCommandList() {
            List<BotCommand> botCommandList = commandList;

            //With this each plugin would have to return empty list instead of null
            //TODO still this is not good solution
            if(botCommandList == null) botCommandList = new ArrayList<>(0);
            return new CommandList(wrapper.getPluginId(), botCommandList);
        }
    }
}
