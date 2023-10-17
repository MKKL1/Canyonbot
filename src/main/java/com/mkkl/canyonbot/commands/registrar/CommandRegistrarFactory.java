package com.mkkl.canyonbot.commands.registrar;

import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.List;

public interface CommandRegistrarFactory {
    CommandRegistrar create(List<ApplicationCommandRequest> requests);
}

