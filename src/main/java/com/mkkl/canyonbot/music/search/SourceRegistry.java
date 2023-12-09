package com.mkkl.canyonbot.music.search;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mkkl.canyonbot.music.search.internal.sources.RegisterSource;
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
        Multimap<Integer, SearchSource> sourcePriorityMap = ArrayListMultimap.create();
        Map<String, Object> sourceBeans = context.getBeansWithAnnotation(RegisterSource.class);

        for (Map.Entry<String, Object> entry : sourceBeans.entrySet()) {
            if (entry.getValue() instanceof SearchSource source) {
                //TODO check for duplicate names
                sourceMap.put(source.identifier(), source);

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

    public Optional<SearchSource> getSource(String identifier) {
        return Optional.ofNullable(sourceMap.getOrDefault(identifier, null));
    }
}
