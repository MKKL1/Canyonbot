package com.mkkl.canyonbot.plugin;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class PluginPath {
    @Getter
    private Path path = Path.of("plugins");
}
