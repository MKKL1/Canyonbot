package com.mkkl.canyonbot.plugin;

import com.mkkl.canyonbot.command.CommandList;
import org.pf4j.ExtensionPoint;

public interface CommandProviderExtension extends ExtensionPoint {
    CommandList getCommandList();
}
