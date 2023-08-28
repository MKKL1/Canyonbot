package com.mkkl.canyonbot.command.registrar;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.interaction.GlobalCommandRegistrar;
import discord4j.rest.interaction.GuildCommandRegistrar;

import java.util.List;

public class GuildCommandRegistrarFactory implements CommandRegistrarFactory {

    private final RestClient restClient;
    private final Snowflake snowflake;

    public GuildCommandRegistrarFactory(RestClient restClient, Snowflake snowflake) {
        this.restClient = restClient;
        this.snowflake = snowflake;
    }

    @Override
    public CommandRegistrar create(List<ApplicationCommandRequest> requests) {
        return new GuildCommandRegistrarImpl(GuildCommandRegistrar.create(restClient, requests), snowflake);
    }
}
