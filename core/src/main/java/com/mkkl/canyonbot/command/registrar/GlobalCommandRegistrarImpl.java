package com.mkkl.canyonbot.command.registrar;

import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.rest.interaction.GlobalCommandRegistrar;
import reactor.core.publisher.Flux;

public class GlobalCommandRegistrarImpl implements CommandRegistrar  {

    private final GlobalCommandRegistrar globalCommandRegistrar;

    public GlobalCommandRegistrarImpl(GlobalCommandRegistrar globalCommandRegistrar) {
        this.globalCommandRegistrar = globalCommandRegistrar;
    }

    @Override
    public Flux<ApplicationCommandData> registerCommands() {
        return globalCommandRegistrar.registerCommands();
    }
}
