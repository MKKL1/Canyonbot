package com.mkkl.canyonbot;

import com.mkkl.canyonbot.commands.registrar.CommandRegistrarFactory;
import com.mkkl.canyonbot.commands.registrar.GlobalCommandRegistrarFactory;
import com.mkkl.canyonbot.commands.registrar.GuildCommandRegistrarFactory;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.rest.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
    @PropertySource("classpath:application.yml"),
    @PropertySource(value = "file:${CONFIG_PATH}", ignoreResourceNotFound = true)
})

@EnableAutoConfiguration
public class BotConfiguration {
    @Value("${bot.commands.guildCommandsEnabled:false}")
    private boolean guildCommandsEnabled;
    @Value("${bot.commands.guildId:0}")
    private long guildId;

    private final RestClient restClient;

    public BotConfiguration(GatewayDiscordClient gatewayDiscordClient) {
        this.restClient = gatewayDiscordClient.getRestClient();
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
