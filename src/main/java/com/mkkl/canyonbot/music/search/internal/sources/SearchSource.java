package com.mkkl.canyonbot.music.search.internal.sources;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;

public interface SearchSource {
    AudioItem search(String query);
}
