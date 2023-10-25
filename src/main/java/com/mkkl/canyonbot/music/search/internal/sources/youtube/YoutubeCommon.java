package com.mkkl.canyonbot.music.search.internal.sources.youtube;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class YoutubeCommon {
    @Bean
    public YoutubeAudioSourceManager youtubeAudioSourceManager() {
        return new YoutubeAudioSourceManager(true, null, null);
    }
}
