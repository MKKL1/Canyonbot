package com.mkkl.canyonbot.music.messages.data;

import com.mkkl.canyonbot.discord.interaction.InteractableButton;
import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.response.ResponseInteraction;
import com.mkkl.canyonbot.discord.utils.TimeoutUtils;
import com.mkkl.canyonbot.music.messages.ResponseFormatUtils;
import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.messages.SearchResponseConst;
import com.mkkl.canyonbot.music.search.sources.SearchSource;
import dev.arbjerg.lavalink.client.player.PlaylistLoaded;
import dev.arbjerg.lavalink.client.player.Track;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Configurable;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

//TODO may need to use lombok anyway
@Configurable
@Value
@Builder
public class ShortPlaylistMessage implements ResponseMessage {
    @NonNull
    PlaylistLoaded playlist; // Represents the loaded playlist
    SearchSource source; // Represents the search source (e.g., where the playlist was found)
    String query; // A query string, potentially describing the playlist retrieval
    @NonNull
    User user; // The user who requested the playlist
    @NonNull
    GatewayDiscordClient gateway; // The Discord client used for Gateway API interactions
    @NonNull
    Function<ButtonInteractionEvent, Mono<?>> onPlay; // A function to handle the playlist's play interactions

    @Override
    public Response getMessage() {
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();

        // Add playlist title and other relevant details
        embedBuilder.title(playlist.getInfo().getName());

        // If available, set the playlist's URI (currently commented out)
        // embedBuilder.url(playlist.getUri()); // TODO: Add playlist URI when available

        // Set the embed color to indicate a successful playlist search
        embedBuilder.color(SearchResponseConst.PLAYLIST_FOUND_COLOR);

        // Add the total playback duration of the playlist
        embedBuilder.addField("Duration", ResponseFormatUtils.formatDuration(
                playlist.getTracks().stream()
                        .map(track -> track.getInfo().getLength())
                        .reduce(0L, Long::sum)
        ), true);

        // Add the number of tracks in the playlist
        embedBuilder.addField("Track count", String.valueOf(playlist.getTracks().size()), true);

        // If the search source is available, add the source field
        if (source != null) {
            embedBuilder.addField("Source", source.name(), true);
        }

        // Add a timestamp to the embed
        embedBuilder.timestamp(Instant.now());

        // Add user information in the footer
        embedBuilder.footer(user.getUsername(), user.getAvatarUrl());

        // Create a "Play" button for the playlist
        InteractableButton playButton = InteractableButton.builder()
                .handler(onPlay)
                .label("Play")
                .build();

        // Build the response
        Response.ResponseBuilder responseBuilder = Response.builder();

        List<EmbedCreateSpec> embedCreateSpecs = new ArrayList<>();
        // If a selected track exists, include its embed details
        if (playlist.getInfo().getSelectedTrack() != -1) {
            embedCreateSpecs.add(Objects.requireNonNull(
                    selectedTrackSpec(playlist.getTracks().get(playlist.getInfo().getSelectedTrack()))
            ));
        }

        // Add the playlist embed to the response
        embedCreateSpecs.add(embedBuilder.build());
        responseBuilder.embeds(embedCreateSpecs);

        // Add the play button as an interaction component
        responseBuilder.components(List.of(ActionRow.of(playButton.asMessageComponent())));

        // Add interaction settings for the play button
        responseBuilder.interaction(ResponseInteraction.builder()
                .interactableComponents(List.of(playButton))
                .gateway(gateway)
                .timeout(Duration.ofSeconds(60)) // 60-second timeout for interactions
                .onTimeout(TimeoutUtils::clearActionBar)
                .build()
        );

        return responseBuilder.build();
    }

    /**
     * Builds an embed spec for the selected track in the playlist.
     *
     * @param selectedTrack The track selected from the playlist
     * @return An embed spec representing the selected track
     */
    private EmbedCreateSpec selectedTrackSpec(Track selectedTrack) {
        // Use the updated AudioTrackMessage to create an embed for the selected track
        AudioTrackMessage message = AudioTrackMessage.builder()
                .audioTrack(selectedTrack)
                .query(query) // Updated to pass the query field directly
                .source(source) // Updated to use the source field directly
                .user(user) // Updated to pass the user field directly
                .build();

        // Retrieve the embed from the AudioTrackMessage
        return message.getMessage()
                .getEmbeds()
                .getFirst();
    }
}
