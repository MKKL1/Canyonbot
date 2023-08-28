package com.mkkl.canyonbot;

import com.mkkl.canyonbot.plugin.BotPluginManager;
import discord4j.core.GatewayDiscordClient;
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
//        List<BotCommand> commands = new ArrayList<>();
//        commands.add(new HelloCommand());


        //PluginCommandLists commandLists = context.getBean(PluginCommandLists.class);
//        for(CommandList commandList : context.getBean(BotPluginManager.class).getExtensions(CommandList.class)) {
//            if(commandList==null) continue;
//            canyonBot.getCommandRegistry().AddCommands(commandList);
//        }
//        canyonBot.getCommandRegistry()
//                .GetGuildCommandRegistrar(canyonBot.getDiscordClient())
//                .registerCommands(Snowflake.of(1143642302435315732L))
//                .subscribe();


        BotPluginManager pluginManager = context.getBean(BotPluginManager.class);
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        RuntimeMode runtimeMode = pluginManager.getRuntimeMode();
        if(runtimeMode == RuntimeMode.DEVELOPMENT) logger.info("Development mode");

        canyonBot.getDiscordClient().withGateway(GatewayDiscordClient::onDisconnect).block();
    }

}
