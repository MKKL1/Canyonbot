package com.mkkl.canyonbot.plugin;

import com.mkkl.canyonbot.CanyonbotApplication;
import com.mkkl.canyonbot.plugin.api.AppPluginContext;
import com.mkkl.canyonbot.plugin.api.PluginContext;
import lombok.Getter;
import lombok.NonNull;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class BotPluginManager extends DefaultPluginManager {

    @Getter
    private AppPluginContext appPluginContext;

    @Autowired
    public BotPluginManager(PluginPath pluginPath, AppPluginContext appPluginContext) {
        super(pluginPath.getPath());
        this.appPluginContext = appPluginContext;
    }

    @Override
    protected PluginFactory createPluginFactory() {
        return new BotPluginFactory();
    }
}
