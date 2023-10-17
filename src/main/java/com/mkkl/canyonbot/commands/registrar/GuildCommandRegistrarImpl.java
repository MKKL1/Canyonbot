package com.mkkl.canyonbot.commands.registrar;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.rest.interaction.GuildCommandRegistrar;
import reactor.core.publisher.Flux;

public class GuildCommandRegistrarImpl implements CommandRegistrar{

    private final GuildCommandRegistrar guildCommandRegistrar;
    private final Snowflake guildId;

    public GuildCommandRegistrarImpl(GuildCommandRegistrar guildCommandRegistrar, Snowflake guildId) {
        this.guildCommandRegistrar = guildCommandRegistrar;
        this.guildId = guildId;
    }


    @Override
    public Flux<ApplicationCommandData> registerCommands() {
        return guildCommandRegistrar.registerCommands(guildId);
    }
}
