package com.mkkl.canyonbot.music.search.internal.sources.youtube;

import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class YoutubeLinkSearch implements SearchSource {
    @Override
    public String name() {
        return "Youtube link";
    }

    @Override
    public String searchIdentifier() {
        return "";
    }

    @Override
    public String logoUrl() {
        return "https://cdn.discordapp.com/attachments/1168970395861397624/1168970843947286649/logo-youtube.png";
    }
}
