package com.mkkl.canyonbot.plugin;

import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;

public abstract class BotPlugin extends SpringPlugin {
    public BotPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }
}
