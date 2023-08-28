package com.mkkl.canyonbot.plugin.api;

import com.mkkl.canyonbot.CanyonBot;
import lombok.Getter;
import org.pf4j.PluginDescriptor;

public record PluginContext(PluginDescriptor pluginDescriptor) {
}
