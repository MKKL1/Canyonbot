package com.mkkl.canyonbot.music.search.sources.youtube;

import com.mkkl.canyonbot.music.search.sources.SearchSource;
import org.springframework.stereotype.Component;

@Component
public class LinkSearch implements SearchSource {
    @Override
    public String name() {
        return "Link";
    }

    @Override
    public String prefix() {
        return "";
    }

}
