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

    @Getter
    private final List<SearchSource> sourceList = new ArrayList<>();

    public SourceRegistry(ApplicationContext context) {
        Multimap<Integer, SearchSource> sourcePriorityMap = ArrayListMultimap.create();
        Map<String, Object> sourceBeans = context.getBeansWithAnnotation(RegisterSource.class);
        for (Map.Entry<String, Object> entry : sourceBeans.entrySet()) {
            if (entry.getValue() instanceof SearchSource source) {
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
}
