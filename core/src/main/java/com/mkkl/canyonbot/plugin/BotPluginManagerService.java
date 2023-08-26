package com.mkkl.canyonbot.plugin;

import com.mkkl.canyonbot.CanyonBot;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class BotPluginManagerService {

    final CanyonBot canyonBot;
    @Autowired
    public BotPluginManagerService(CanyonBot canyonBot) {
        this.canyonBot = canyonBot;
    }

    @Bean
    private SpringPluginManager botPluginManager() {
        return new SpringPluginManager(Path.of("plugins"));
    }
}
