package com.mkkl.canyonbot.music;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.List;

@AllArgsConstructor
@ConfigurationProperties(prefix = "player")
@ConfigurationPropertiesScan
public class PlayerConfigurationProperties {
    List<NodeOptions> nodes;

    record NodeOptions(String name, String uri, String password, Long httpTimeout) {
    }
}
