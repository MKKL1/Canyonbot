package com.mkkl.canyonbot.music.search;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.music.search.internal.sources.RegisterSource;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SourceRegistry {

    @Getter
    private final List<SearchSource> sourceList = new ArrayList<>();

    public SourceRegistry(ApplicationContext context) {
        Map<String,Object> sourceBeans = context.getBeansWithAnnotation(RegisterSource.class);
        for (Map.Entry<String, Object> entry : sourceBeans.entrySet()) {
            if (entry.getValue() instanceof SearchSource) {
                sourceList.add((SearchSource) entry.getValue());
            }
        }
    }
}
