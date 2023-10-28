package com.mkkl.canyonbot.commands.completion;

import com.austinv11.servicer.Service;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.suggest.Lookup;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandOptionCompletionManager {
    private final Map<String, CommandOptionCompletion> completions;
    public CommandOptionCompletionManager() {
        this.completions = new HashMap<>();
    }

    public void addOption(String optionName, CommandOptionCompletion completion) {
        completions.put(optionName, completion);
    }

    public Mono<Void> handleAutoCompletionEvent(ChatInputAutoCompleteEvent event) {
        ApplicationCommandInteractionOption option = event.getFocusedOption();
        String optionName = option.getName();
        if(completions.containsKey(optionName)) {
            return completions.get(optionName).handleCommandInteraction(option).flatMap(results -> {
                List<ApplicationCommandOptionChoiceData> suggestions = new ArrayList<>();
                for (Lookup.LookupResult result : results) {
                    suggestions.add(ApplicationCommandOptionChoiceData.builder()
                            .name(result.key.toString())
                            .value(result.key.toString())
                            .build());
                }
                return event.respondWithSuggestions(suggestions);
            }).doOnError(Throwable::printStackTrace);
        }

        return Mono.empty();
    }
}
