package com.mkkl.canyonbot.music.messages;

import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.time.Instant;

public class TrackMessageBuilder implements SearchResponseMessage {

    private final EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();

    private TrackMessageBuilder(AudioTrack audioTrack) {
        embedBuilder.title(audioTrack.getInfo().title);
        embedBuilder.url(audioTrack.getInfo().uri);
        embedBuilder.color(SearchResponseConst.TRACK_FOUND_COLOR);
        embedBuilder.addField("Duration", ResponseFormatUtils.formatDuration(audioTrack.getDuration()), true);

        embedBuilder.timestamp(Instant.now());
        if(audioTrack.getInfo().artworkUrl != null) embedBuilder.image(audioTrack.getInfo().artworkUrl);

    }

    public static TrackMessageBuilder create(AudioTrack audioTrack) {
        return new TrackMessageBuilder(audioTrack);
    }

    public TrackMessageBuilder setSource(SearchSource source) {
        if(source.logoUrl() != null) embedBuilder.thumbnail(source.logoUrl());
        embedBuilder.addField("Source", source.name(), true);
        return this;
    }

    public TrackMessageBuilder setQuery(String query) {
        embedBuilder.description(query);
        return this;
    }

    public TrackMessageBuilder setUser(User user) {
        embedBuilder.footer(user.getUsername(), user.getAvatarUrl());
        return this;
    }

    @Override
    public EmbedCreateSpec getSpecResponse() {
        return embedBuilder.build();
    }
}
