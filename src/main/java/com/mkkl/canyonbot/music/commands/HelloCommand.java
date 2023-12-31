package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

@RegisterCommand
public class HelloCommand extends BotCommand {

    public HelloCommand(DefaultErrorHandler errorHandler) {
        super(ApplicationCommandRequest.builder()
                .name("hello")
                .description("Say hello")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("user")
                        .type(ApplicationCommandOption.Type.USER.getValue())
                        .description("User to say hello to")
                        .required(true).build())
                .build(), errorHandler);
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.reply("hello " + event.getInteraction().getCommandInteraction().get().getOption("user").get().getValue().get().asUser().block().getTag());
    }
}
