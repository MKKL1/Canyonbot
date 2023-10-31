package com.mkkl.canyonbot.music.messages;

import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionReplyEditSpec;

import java.time.Instant;

//TODO to builder
public class TrackMessage implements SearchResponseMessage {

    private final EmbedCreateSpec specResponse;

    private TrackMessage(AudioTrack audioTrack, SearchSource source) {
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
        embedBuilder.title(audioTrack.getInfo().title);
        embedBuilder.url(audioTrack.getInfo().uri);
        embedBuilder.description(audioTrack.getInfo().author);
        embedBuilder.addField("Duration", ResponseFormatUtils.formatDuration(audioTrack.getDuration()), true);
        embedBuilder.timestamp(Instant.now());
        if(source.logoUrl() != null) embedBuilder.thumbnail(source.logoUrl());
        if(audioTrack.getInfo().artworkUrl != null) embedBuilder.image(audioTrack.getInfo().artworkUrl);

        specResponse = embedBuilder.build();
    }

    public static TrackMessage create(AudioTrack audioTrack, SearchSource source) {
        return new TrackMessage(audioTrack, source);
    }

    @Override
    public EmbedCreateSpec getSpecResponse() {
        return specResponse;
    }
}
