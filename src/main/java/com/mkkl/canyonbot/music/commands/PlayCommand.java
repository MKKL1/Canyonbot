package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.music.search.SearchManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@RegisterCommand
public class PlayCommand extends BotCommand {
    private final SearchManager searchManager;

    public PlayCommand(SearchManager searchManager) {
        super(ApplicationCommandRequest.builder()
                .name("play")
                .description("Play a song")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("url")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .description("Song url to play")
                        .required(true)
                        .build())
                .build());
        this.searchManager = searchManager;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        String query = event.getInteraction()
                .getCommandInteraction()
                .flatMap(commandInteraction -> commandInteraction.getOption("url"))
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElseThrow();//TODO throw exception
        return event.deferReply()
                .then(handleQuery(event, query))
                .then();
    }

    private Mono<Message> handleQuery(ChatInputInteractionEvent event, String query) {
        return searchManager.search(query)
                .flatMap(searchResult -> {
                    Mono<Message> message = event.editReply("No match found");
                    if (searchResult.getPlaylists() != null && !searchResult.getPlaylists()
                            .isEmpty()) {
                        message = event.editReply("Loaded playlist " + searchResult.getPlaylists()
                                .getFirst()
                                .getTracks()
                                .stream()
                                .map(audioTrack -> audioTrack.getInfo().title)
                                .reduce("", (s, s2) -> s + "\n" + s2));
                    } else if (searchResult.getTracks() != null && !searchResult.getTracks().isEmpty()) {
                        message = event.editReply("Loaded tracks " + searchResult.getTracks().stream()
                                .map(audioTrack -> audioTrack.getInfo().title)
                                .reduce("", (s, s2) -> s + "\n" + s2));
                    }

                    return message;
                })
                .onErrorResume(throwable -> event.editReply("Error:" + throwable.getMessage()));
    }
}
