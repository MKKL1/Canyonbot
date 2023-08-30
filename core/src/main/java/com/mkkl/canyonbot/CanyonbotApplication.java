package com.mkkl.canyonbot;

import com.mkkl.canyonbot.command.CommandRegistry;
import com.mkkl.canyonbot.plugin.BotPluginManager;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import org.pf4j.RuntimeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CanyonbotApplication {

    static Logger logger = LoggerFactory.getLogger(CanyonbotApplication.class);


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CanyonbotApplication.class, args);


        CanyonBot canyonBot = context.getBean(CanyonBot.class);

        BotPluginManager pluginManager = context.getBean(BotPluginManager.class);
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        CommandRegistry commandRegistry = context.getBean(CommandRegistry.class);
        commandRegistry.GetCommandRegistrar().registerCommands().subscribe();

        RuntimeMode runtimeMode = pluginManager.getRuntimeMode();
        if(runtimeMode == RuntimeMode.DEVELOPMENT) logger.info("Development mode");
        canyonBot.getDiscordClient().withGateway(GatewayDiscordClient::onDisconnect).block();
    }

}
