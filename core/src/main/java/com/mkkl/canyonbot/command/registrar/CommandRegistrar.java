package com.mkkl.canyonbot.command.registrar;

import discord4j.discordjson.json.ApplicationCommandData;
import reactor.core.publisher.Flux;

public interface CommandRegistrar {
    Flux<ApplicationCommandData> registerCommands();
}
