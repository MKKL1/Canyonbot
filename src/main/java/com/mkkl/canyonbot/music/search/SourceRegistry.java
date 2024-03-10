package com.mkkl.canyonbot.music.search;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
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
        Map<String, SearchSource> sourceBeans = context.getBeansOfType(SearchSource.class);

        for (Map.Entry<String, SearchSource> entry : sourceBeans.entrySet()) {
            SearchSource source = entry.getValue();
            //TODO check for duplicate names
            sourceMap.put(source.searchIdentifier(), source);
        }
    }

    public Optional<SearchSource> getSource(String identifier) {
        return Optional.ofNullable(sourceMap.getOrDefault(identifier, null));
    }
}
