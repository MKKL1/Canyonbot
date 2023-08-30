package com.mkkl.canyonbot.musicbot.commands;

import com.mkkl.canyonbot.command.BotCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class PlayCommand extends BotCommand {

    public PlayCommand() {
        super(ApplicationCommandRequest.builder()
                .name("play")
                .description("Play a song")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("url or search term")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .description("Url or search term to play song from")
                        .required(true).build())
                .build());
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.reply("hello " + event.getInteraction().getCommandInteraction().get().getOption("user").get().getValue().get().asUser().block().getTag());
    }
}
