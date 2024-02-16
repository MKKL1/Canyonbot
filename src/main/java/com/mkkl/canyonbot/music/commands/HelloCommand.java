package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.discord.interaction.CustomButton;
import com.mkkl.canyonbot.discord.interaction.ImmutableCustomButton;
import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.response.ResponseInteraction;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

@RegisterCommand
public class HelloCommand extends BotCommand {

    @Autowired
    private Mono<GatewayDiscordClient> gateway;
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
        CustomButton button = ImmutableCustomButton.builder().label("b").interaction(eventb -> eventb.reply("c")).build();
        Response response = Response.builder()
                .content("hello")
                .addComponent(ActionRow.of(button.asMessageComponent()))
                .interaction(ResponseInteraction.builder()
                        .addInteractableComponent(button)
                        .gateway(gateway)
                        .build())
                .build();
        return event.reply(response.asCallbackSpec())
                .then(event.getReply())
                .flatMap(reply -> response.getResponseInteraction().get().interaction(reply));
    }
}
