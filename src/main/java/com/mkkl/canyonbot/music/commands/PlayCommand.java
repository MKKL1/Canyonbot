package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.AutoCompleteCommand;
import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.commands.completion.CommandOptionCompletion;
import com.mkkl.canyonbot.commands.completion.CommandOptionCompletionManager;
import com.mkkl.canyonbot.music.search.SearchManager;
import com.mkkl.canyonbot.music.search.SourceRegistry;
import com.mkkl.canyonbot.music.search.internal.sources.SourceSuggestionOption;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RegisterCommand
public class PlayCommand extends BotCommand implements AutoCompleteCommand {
    private final SearchManager searchManager;
    private CommandOptionCompletionManager completionManager;
    public PlayCommand(SearchManager searchManager, SourceRegistry sourceRegistry) {
        super(ApplicationCommandRequest.builder()
                .name("play")
                .description("Play a song")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("query")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .description("url or search query")
                        .required(true)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name("source")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .description("source to search from")
                        .required(false)
                        .autocomplete(true)
                        .build())
                .build());
        completionManager = new CommandOptionCompletionManager();
        completionManager.addOption("source", new CommandOptionCompletion(sourceRegistry.sourceSuggestionOptions()));
        this.searchManager = searchManager;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        String query = event.getInteraction()
                .getCommandInteraction()
                .flatMap(commandInteraction -> commandInteraction.getOption("query"))
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
                    } else if (searchResult.getTracks() != null && !searchResult.getTracks()
                            .isEmpty()) {
                        message = event.editReply("Loaded tracks " + searchResult.getTracks()
                                .stream()
                                .map(audioTrack -> audioTrack.getInfo().title)
                                .reduce("", (s, s2) -> s + "\n" + s2));
                    }

                    return message;
                })
                .onErrorResume(throwable -> event.editReply("Error:" + throwable.getMessage()));
    }

    @Override
    public Mono<Void> autoComplete(ChatInputAutoCompleteEvent event) {
        //TODO use scheduler
        return completionManager.handleAutoCompletionEvent(event);//TODO redis may be used to perform searches
    }
}
