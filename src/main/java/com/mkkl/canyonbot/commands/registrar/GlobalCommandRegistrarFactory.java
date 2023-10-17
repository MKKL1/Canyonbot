package com.mkkl.canyonbot.commands.registrar;

import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.interaction.GlobalCommandRegistrar;

import java.util.List;

public class GlobalCommandRegistrarFactory implements CommandRegistrarFactory {

    private final RestClient restClient;

    public GlobalCommandRegistrarFactory(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public CommandRegistrar create(List<ApplicationCommandRequest> requests) {
        return new GlobalCommandRegistrarImpl(GlobalCommandRegistrar.create(restClient, requests));
    }
}
