package com.mkkl.canyonbot.commands.completion;

import com.austinv11.servicer.Service;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import org.apache.lucene.document.Document;
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
            return completions.get(optionName).handleCommandInteraction(option).flatMap(documents -> {
                List<ApplicationCommandOptionChoiceData> suggestions = new ArrayList<>();
                for (Document document : documents) {
                    System.out.println(document);
//                    suggestions.add(ApplicationCommandOptionChoiceData.builder()
//                            .name(document.get("name"))
//                            .value(document.get("value"))
//                            .build());
                }
                return event.respondWithSuggestions(suggestions);
            }).doOnError(Throwable::printStackTrace);

//            List<ApplicationCommandOptionChoiceData> suggestions = new ArrayList<>();
//            suggestions.add(ApplicationCommandOptionChoiceData.builder().name("Thing 1").value("value").build());
//            suggestions.add(ApplicationCommandOptionChoiceData.builder().name("Something 2").value("other").build());
//            suggestions.add(ApplicationCommandOptionChoiceData.builder().name("some other input").value("pick me").build());
//            return event.respondWithSuggestions(suggestions);
        }

        return Mono.empty();
    }
}
