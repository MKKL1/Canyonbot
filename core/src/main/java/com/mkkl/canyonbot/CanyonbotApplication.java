package com.mkkl.canyonbot;

import com.mkkl.canyonbot.command.BotCommand;
import com.mkkl.canyonbot.command.CommandList;
import com.mkkl.canyonbot.command.HelloCommand;
import com.mkkl.canyonbot.plugin.PluginCommandLists;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import org.pf4j.RuntimeMode;
import org.pf4j.spring.SpringPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CanyonbotApplication {

    static Logger logger = LoggerFactory.getLogger(CanyonbotApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CanyonbotApplication.class, args);


        CanyonBot canyonBot = context.getBean(CanyonBot.class);
//        List<BotCommand> commands = new ArrayList<>();
//        commands.add(new HelloCommand());


        //PluginCommandLists commandLists = context.getBean(PluginCommandLists.class);
        for(CommandList commandList : context.getBean(SpringPluginManager.class).getExtensions(CommandList.class)) {
            if(commandList==null) continue;
            canyonBot.getCommandRegistry().AddCommands(commandList);
        }
        canyonBot.getCommandRegistry()
                .GetGuildCommandRegistrar(canyonBot.getDiscordClient())
                .registerCommands(Snowflake.of(1143642302435315732L))
                .subscribe();


        SpringPluginManager pluginManager = context.getBean(SpringPluginManager.class);
        //pluginManager.startPlugins();
        RuntimeMode runtimeMode = pluginManager.getRuntimeMode();
        if(runtimeMode == RuntimeMode.DEVELOPMENT) logger.info("Development mode");

        canyonBot.getDiscordClient().withGateway(GatewayDiscordClient::onDisconnect).block();
    }

}
