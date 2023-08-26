package com.mkkl.canyonbot.plugin;

import com.mkkl.canyonbot.command.CommandList;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PluginCommandLists {
    @Getter
    private final List<CommandList> commandLists;

    @Autowired
    public PluginCommandLists(List<CommandList> commandLists) {
        this.commandLists = commandLists;
    }
}
