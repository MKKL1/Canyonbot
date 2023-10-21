package com.mkkl.canyonbot.music.search;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SourceRegistry {

    @Getter
    private final List<AudioSourceManager> sourceList = new ArrayList<>();

    public SourceRegistry() {
        sourceList.add(new YoutubeAudioSourceManager(true, null, null));
    }
}
