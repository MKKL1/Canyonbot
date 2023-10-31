package com.mkkl.canyonbot.music.messages;

import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder(setterPrefix = "set")
@Getter
public class AudioTrackMessage implements ResponseMessage {

    private AudioTrack audioTrack;
    private SearchSource source;
    private String query;
    private User user;

    public EmbedCreateSpec getSpec() {
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
        embedBuilder.title(audioTrack.getInfo().title);
        embedBuilder.description(query);
        embedBuilder.thumbnail(source.logoUrl());
        embedBuilder.url(audioTrack.getInfo().uri);
        embedBuilder.color(SearchResponseConst.TRACK_FOUND_COLOR);
        embedBuilder.addField("Duration", ResponseFormatUtils.formatDuration(audioTrack.getDuration()), true);
        embedBuilder.addField("Source", source.name(), true);
        //embedBuilder.timestamp(audioTrack.getInfo().isStream ? null : audioTrack.getInfo().creationTime);//TODO add creation time to audio track
        embedBuilder.timestamp(Instant.now());
        if(audioTrack.getInfo().artworkUrl != null) embedBuilder.image(audioTrack.getInfo().artworkUrl);
        embedBuilder.footer(user.getUsername(), user.getAvatarUrl());
        return embedBuilder.build();
    }
}
