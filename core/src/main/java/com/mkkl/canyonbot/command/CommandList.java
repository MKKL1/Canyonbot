package com.mkkl.canyonbot.command;

import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public record CommandList(String moduleName, List<BotCommand> commandList) implements Iterable<BotCommand> {

    @Override
    public Iterator<BotCommand> iterator() {
        return commandList.iterator();
    }

    public List<ApplicationCommandRequest> GetCommandRequests() {
        return commandList.stream()
                .map(BotCommand::getCommandRequest)
                .toList();
    }


}
