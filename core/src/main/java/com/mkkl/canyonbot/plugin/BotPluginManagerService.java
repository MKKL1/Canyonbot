package com.mkkl.canyonbot.plugin;

import com.mkkl.canyonbot.CanyonBot;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class BotPluginManagerService {

    private final ApplicationContext applicationContext;

    @Autowired
    public BotPluginManagerService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    private SpringPluginManager botPluginManager() {
        SpringPluginManager springPluginManager = new SpringPluginManager(Path.of("plugins"));
        springPluginManager.setApplicationContext(applicationContext);
        return springPluginManager;
    }
}
