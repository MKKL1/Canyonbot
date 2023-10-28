package com.mkkl.canyonbot.music.search;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mkkl.canyonbot.commands.completion.SuggestionOption;
import com.mkkl.canyonbot.music.search.internal.sources.RegisterSource;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.mkkl.canyonbot.music.search.internal.sources.SourceSuggestionOption;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SourceRegistry {

    //TODO it seems unsafe to share mutable list like this
    @Getter
    private final List<SearchSource> sourceList = new ArrayList<>();

    //Map used to access sources by name
    private final Map<String, SearchSource> sourceMap = new HashMap<>();

    public SourceRegistry(ApplicationContext context) {
        Multimap<Integer, SearchSource> sourcePriorityMap = ArrayListMultimap.create();
        Map<String, Object> sourceBeans = context.getBeansWithAnnotation(RegisterSource.class);

        for (Map.Entry<String, Object> entry : sourceBeans.entrySet()) {
            if (entry.getValue() instanceof SearchSource source) {
                //TODO check for duplicate names
                sourceMap.put(source.name(), source);

                sourcePriorityMap.put(source.getClass()
                        .getAnnotation(RegisterSource.class)
                        .priority(), source);
            }
        }
        sourcePriorityMap.entries()
                .stream()
                .sorted((o1, o2) -> o2.getKey() - o1.getKey())
                .forEachOrdered(e -> sourceList.add(e.getValue()));
    }

    public Optional<SearchSource> getSource(String name) {
        return Optional.ofNullable(sourceMap.getOrDefault(name, null));
    }

    public List<SuggestionOption> sourceSuggestionOptions() {
        List<SuggestionOption> options = new ArrayList<>();
        for (SearchSource source : sourceList) {
            options.add(new SourceSuggestionOption(source));
        }
        return options;
    }
}
