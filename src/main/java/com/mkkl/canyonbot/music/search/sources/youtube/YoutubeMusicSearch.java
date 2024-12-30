package com.mkkl.canyonbot.music.search.sources.youtube;

import com.mkkl.canyonbot.music.search.sources.SearchSource;
import org.springframework.stereotype.Component;

@Component
public class YoutubeMusicSearch implements SearchSource {
    @Override
    public String name() {
        return "YoutubeMusic search";
    }

    @Override
    public String prefix() {
        return "ytmsearch:";
    }

    @Override
    public String logoUrl() {
        return "https://cdn.discordapp.com/attachments/1168970395861397624/1168982281029963856/Youtube_Music_icon.png";
    }
}
