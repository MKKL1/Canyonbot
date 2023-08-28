package com.mkkl.canyonbot.plugin;

import com.mkkl.canyonbot.plugin.api.AppPluginContext;
import com.mkkl.canyonbot.plugin.api.PluginContext;
import org.pf4j.DefaultPluginFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

@Component
public class BotPluginFactory extends DefaultPluginFactory {

    private static final Logger log = LoggerFactory.getLogger(BotPluginFactory.class);

    @Override
    protected Plugin createInstance(Class<?> pluginClass, PluginWrapper pluginWrapper) {
        try {
            PluginContext pluginContext = new PluginContext(pluginWrapper.getDescriptor());
            //Quick fix
            AppPluginContext appPluginContext = ((BotPluginManager)pluginWrapper.getPluginManager()).getAppPluginContext();

            Constructor<?> constructor = pluginClass.getConstructor(PluginContext.class, AppPluginContext.class);
            return (Plugin) constructor.newInstance(pluginContext, appPluginContext);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
