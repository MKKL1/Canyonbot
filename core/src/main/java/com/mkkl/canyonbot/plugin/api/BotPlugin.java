package com.mkkl.canyonbot.plugin.api;

import org.pf4j.Plugin;

public abstract class BotPlugin extends Plugin {
    protected final AppPluginContext appPluginContext;
    protected final PluginContext pluginContext;
    protected BotPlugin(PluginContext pluginContext, AppPluginContext appPluginContext) {
        super();
        this.pluginContext = pluginContext;
        this.appPluginContext = appPluginContext;
    }
}
