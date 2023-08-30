package com.mkkl.canyonbot.db;


import com.mkkl.canyonbot.plugin.api.AppPluginContext;
import com.mkkl.canyonbot.plugin.api.BotPlugin;
import com.mkkl.canyonbot.plugin.api.PluginContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DatabasePlugin extends BotPlugin {

    public DatabasePlugin(PluginContext pluginContext, AppPluginContext appPluginContext) {
        super(pluginContext, appPluginContext);
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        return new AnnotationConfigApplicationContext();
    }
}
