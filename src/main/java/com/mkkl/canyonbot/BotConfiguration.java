package com.mkkl.canyonbot;

import com.mkkl.canyonbot.commands.registrar.CommandRegistrarFactory;
import com.mkkl.canyonbot.commands.registrar.GlobalCommandRegistrarFactory;
import com.mkkl.canyonbot.commands.registrar.GuildCommandRegistrarFactory;
import discord4j.common.util.Snowflake;
import discord4j.rest.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
@Configuration
public class BotConfiguration {
    @Value("${bot.commands.guildCommandsEnabled:false}")
    private boolean guildCommandsEnabled;
    @Value("${bot.commands.guildId:0}")
    private long guildId;

    private final RestClient restClient;

    public BotConfiguration(RestClient restClient) {
        this.restClient = restClient;
    }

    @Bean
    public CommandRegistrarFactory commandRegistrarFactory() {
        if(guildCommandsEnabled) {
            if(guildId == 0) {
                throw new IllegalArgumentException("Guild commands are enabled but no guild id is provided");
            }
            return new GuildCommandRegistrarFactory(restClient, Snowflake.of(guildId));
        }
        else {
            return new GlobalCommandRegistrarFactory(restClient);
        }
    }
}
