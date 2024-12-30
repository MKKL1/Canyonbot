package com.mkkl.canyonbot.music.search;
import com.mkkl.canyonbot.music.search.sources.SearchSource;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SourceRegistry {

    @Getter
    private final List<SearchSource> sourceList = new ArrayList<>();

    private final Map<String, SearchSource> sourceMap = new ConcurrentHashMap<>();

    public SourceRegistry(ApplicationContext context) {
        Map<String, SearchSource> sourceBeans = context.getBeansOfType(SearchSource.class);

        for (Map.Entry<String, SearchSource> entry : sourceBeans.entrySet()) {
            SearchSource source = entry.getValue();
//            if (sourceMap.containsKey(source.searchIdentifier())) {
//                throw new IllegalArgumentException("Duplicate searchIdentifier: " + source.searchIdentifier());
//            }
            sourceList.add(source);
            sourceMap.put(source.prefix(), source);
        }
    }

    public SearchSource getSource(String identifier) {
        SearchSource source = sourceMap.get(identifier);
        if (source == null) {
            throw new InvalidParameterException("Source not found for identifier: " + identifier);
        }
        return source;
    }
}
