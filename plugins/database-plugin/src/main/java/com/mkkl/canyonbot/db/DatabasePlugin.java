package com.mkkl.canyonbot.db;

import com.mkkl.canyonbot.plugin.BotPlugin;
import org.laxture.sbp.spring.boot.SpringBootstrap;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;

public class DatabasePlugin extends BotPlugin {
    public DatabasePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        return null;
    }

    @Override
    public void start() {
        super.start();
        System.out.println("db");
    }
}
