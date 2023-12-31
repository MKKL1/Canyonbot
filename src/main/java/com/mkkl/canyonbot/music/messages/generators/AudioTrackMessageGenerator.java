package com.mkkl.canyonbot.music.messages.generators;

import com.mkkl.canyonbot.music.messages.ResponseFormatUtils;
import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.messages.SearchResponseConst;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Optional;

@Value.Immutable
public interface AudioTrackMessageGenerator extends ResponseMessage {

    AudioTrack audioTrack();

    Optional<SearchSource> source();

    Optional<String> query();

    Optional<User> user();

    @Override
    default ResponseMessageData getMessage() {
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
        embedBuilder.title(audioTrack().getInfo().title);
        if(query().isPresent()) embedBuilder.description(query().get());
        if(source().isPresent()) {
            embedBuilder.thumbnail(source().get().logoUrl());
            embedBuilder.addField("Source", source().get().name(), true);
        }
        embedBuilder.url(audioTrack().getInfo().uri);
        embedBuilder.color(SearchResponseConst.TRACK_FOUND_COLOR);
        embedBuilder.addField("Duration", ResponseFormatUtils.formatDuration(audioTrack().getDuration()), true);
        //embedBuilder.timestamp(audioTrack.getInfo().isStream ? null : audioTrack.getInfo().creationTime);//TODO add creation time to audio track
        embedBuilder.timestamp(Instant.now());
        if(audioTrack().getInfo().artworkUrl != null) embedBuilder.image(audioTrack().getInfo().artworkUrl);
        if(user().isPresent()) embedBuilder.footer(user().get().getUsername(), user().get().getAvatarUrl());
        return ResponseMessageData.builder()
                .addEmbed(embedBuilder.build())
                .build();
    }
}
