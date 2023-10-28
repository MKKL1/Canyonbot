package com.mkkl.canyonbot.commands.completion;

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.suggest.DocumentDictionary;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CommandOptionCompletion { //TODO interface

    private final int MAX_RESULTS = 25;

    private final Directory directory = new ByteBuffersDirectory();
    private AnalyzingInfixSuggester suggester;
    private List<Lookup.LookupResult> defaultResults;
    public CommandOptionCompletion(List<SuggestionOption> options) {

        try {
            suggester = new AnalyzingInfixSuggester(directory, new StandardAnalyzer());
            suggester.build(new OptionSuggestionIterator(options.iterator()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        defaultResults = new ArrayList<>();

        for (SuggestionOption option : options) {
            defaultResults.add(new Lookup.LookupResult(option.getName(), option.getWeight()));
            if (defaultResults.size() >= MAX_RESULTS) break;
        }
    }

    public Mono<List<Lookup.LookupResult>> handleCommandInteraction(ApplicationCommandInteractionOption option) {
        String typing = option.getValue()
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElse("");
        if(typing.isEmpty()) return Mono.just(defaultResults);
        try {
            return Mono.just(suggester.lookup(typing, 10, true, true))
                    .flatMap(lookupResults -> Mono.just(new ArrayList<>(lookupResults)));
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
