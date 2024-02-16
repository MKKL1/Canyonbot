package com.mkkl.canyonbot.music.messages.generators;

import com.mkkl.canyonbot.discord.interaction.CustomButton;
import com.mkkl.canyonbot.discord.interaction.ImmutableCustomButton;
import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.response.ResponseInteraction;
import com.mkkl.canyonbot.discord.utils.TimeoutUtils;
import com.mkkl.canyonbot.music.messages.ResponseFormatUtils;
import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.mkkl.canyonbot.music.messages.SearchResponseConst;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import org.immutables.value.Value;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Configurable;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

//TODO may need to use lombok anyway
@Configurable
@Value.Immutable
public interface ShortPlaylistMessageGenerator extends ResponseMessage {
    AudioPlaylist playlist();
    Optional<SearchSource> source();
    Optional<String> query();
    User user();
    Mono<GatewayDiscordClient> gateway();
    Function<ButtonInteractionEvent, Publisher<?>> onPlay();

    @Override
    default Response getMessage() {
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
        embedBuilder.title(playlist().getName());
        //embedBuilder.url(playlist.getUri()); //TODO add playlist uri
        embedBuilder.color(SearchResponseConst.PLAYLIST_FOUND_COLOR);
        embedBuilder.addField("Duration", ResponseFormatUtils.formatDuration(playlist().getTracks()
                .stream()
                .map(AudioTrack::getDuration)
                .reduce(0L, Long::sum)), true);
        embedBuilder.addField("Track count", String.valueOf(playlist().getTracks()
                .size()), true);
        if (source().isPresent())
            embedBuilder.addField("Source", source().get()
                    .name(), true);
        //embedBuilder.timestamp(audioTrack.getInfo().isStream ? null : audioTrack.getInfo().creationTime);//TODO add creation time to audio track
        embedBuilder.timestamp(Instant.now());
//        if (audioTrack.getInfo().artworkUrl != null) embedBuilder.image(audioTrack.getInfo().artworkUrl);
        embedBuilder.footer(user()
                .getUsername(), user()
                .getAvatarUrl());

        CustomButton playButton = ImmutableCustomButton.builder()
                .interaction(onPlay())
                .label("Play")
                .build();
        Response.Builder responseBuilder = Response.builder();
        AudioTrack selectedTrack = playlist().getSelectedTrack();
        if (selectedTrack != null)
            responseBuilder.addEmbed(Objects.requireNonNull(selectedTrackSpec(selectedTrack)));
        responseBuilder.addEmbed(embedBuilder.build());
        responseBuilder.addComponent(ActionRow.of(playButton.asMessageComponent()));
        responseBuilder.interaction(ResponseInteraction.builder()
                .addInteractableComponent(playButton)
                .gateway(gateway())
                .timeout(Duration.ofSeconds(60))
                .onTimeout(TimeoutUtils::clearActionBar)
                .build());

        return responseBuilder.build();
    }

    private EmbedCreateSpec selectedTrackSpec(AudioTrack selectedTrack) {
        AudioTrackMessage.Builder builder = AudioTrackMessage.builder();
        builder.audioTrack(selectedTrack);
        builder.query(query());
        builder.source(source());
        builder.user(user());
        return builder.build()
                .getMessage()
                .embeds()
                .getFirst();
    }

}
