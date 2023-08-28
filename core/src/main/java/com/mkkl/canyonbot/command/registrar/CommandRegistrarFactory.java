package com.mkkl.canyonbot.command.registrar;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.interaction.GlobalCommandRegistrar;
import discord4j.rest.interaction.GuildCommandRegistrar;

import java.util.List;

public interface CommandRegistrarFactory {
    CommandRegistrar create(List<ApplicationCommandRequest> requests);
}

