package com.mkkl.canyonbot.music.messages.data;

import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.music.messages.ResponseFormatUtils;
import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.messages.SearchResponseConst;
import com.mkkl.canyonbot.music.search.sources.SearchSource;
import dev.arbjerg.lavalink.client.player.Track;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Value
@Builder
public class AudioTrackMessage implements ResponseMessage {
    @NonNull
    Track audioTrack; // Represents the audio track details
    SearchSource source; // Represents the search source (e.g., from where it was found)
    String query; // The search query related to the track
    User user; // The user who requested the audio track

    @Override
    public Response getMessage() {
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();

        // Setting the track's title
        embedBuilder.title(audioTrack.getInfo().getTitle());

        // If a query is present, set it as the description
        if (query != null && !query.isEmpty()) {
            embedBuilder.description(query);
        }

        // If the source is present, add its details
        if (source != null) {
            embedBuilder.thumbnail(source.logoUrl());
            embedBuilder.addField("Source", source.name(), true);
        }

        // Add the track's URL if available
        if (audioTrack.getInfo().getUri() != null) {
            embedBuilder.url(Objects.requireNonNull(audioTrack.getInfo().getUri()));
        }

        // Set the embed color (constant for successful track search)
        embedBuilder.color(SearchResponseConst.TRACK_FOUND_COLOR);

        // Add the track's duration field
        embedBuilder.addField("Duration", ResponseFormatUtils.formatDuration(audioTrack.getInfo().getLength()), true);

        // Add a timestamp to the embed
        embedBuilder.timestamp(Instant.now());

        // If an artwork URL is available for the track, include it
        if (audioTrack.getInfo().getArtworkUrl() != null) {
            embedBuilder.image(Objects.requireNonNull(audioTrack.getInfo().getArtworkUrl()));
        }

        // If user information is available, set it in the footer
        if (user != null) {
            embedBuilder.footer(user.getUsername(), user.getAvatarUrl());
        }

        // Build and return the response
        return Response.builder()
                .embeds(List.of(embedBuilder.build()))
                .build();
    }
}
