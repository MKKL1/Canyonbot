package com.mkkl.canyonbot.plugin.api;

import com.mkkl.canyonbot.CanyonBot;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppPluginContext {
    @Getter
    private CanyonBot canyonBot;

    @Autowired
    public AppPluginContext(CanyonBot canyonBot) {
        this.canyonBot = canyonBot;
    }
}
