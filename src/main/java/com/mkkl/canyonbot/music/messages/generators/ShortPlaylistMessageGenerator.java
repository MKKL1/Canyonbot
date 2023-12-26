package com.mkkl.canyonbot.music.messages.generators;

import com.mkkl.canyonbot.music.messages.ResponseFormatUtils;
import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.mkkl.canyonbot.music.messages.SearchResponseConst;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import org.immutables.value.Value;
import org.springframework.beans.factory.annotation.Configurable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

//TODO may need to use lombok anyway
@Configurable
@Value.Immutable
public interface ShortPlaylistMessageGenerator extends ResponseMessage {
    AudioPlaylist playlist();

    Optional<SearchSource> source();

    Optional<String> query();

    User user();

    @Override
    default ResponseMessageData getMessage() {
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

        ResponseMessageData.Builder responseBuilder = ResponseMessageData.builder();
        AudioTrack selectedTrack = playlist().getSelectedTrack();
        if (selectedTrack != null)
            responseBuilder.addEmbed(Objects.requireNonNull(selectedTrackSpec(selectedTrack)));
        responseBuilder.addEmbed(embedBuilder.build());

        //TODO for now I am handling button behavior here, but it should go somewhere else.
        // Maybe create class derived from TempListenableButton that will define behavior of play button inside it's definition
//        TempListenableButton button = TempListenableButton.builder(Button.primary("play", "Play"))
//                .clickAction(event ->
//                        Mono.just(event)
//                        .zipWith(Mono.just(TrackQueueElement.listOf(playlist().getTracks(), user())))
//                        .doOnNext((tuple -> trackQueueService().addAll(guild(), tuple.getT2())))
//                        .flatMap(tuple -> tuple.getT1().reply("Playing " + tuple.getT2().size() + " tracks"))
//                )
//                .timeoutAction(() -> Mono.fromRunnable(() -> System.out.println("button timed out")))
//                .build();
//        responseBuilder.publisher(button.register(client(), Duration.ofMinutes(60)));
        responseBuilder.addComponent(ActionRow.of(Button.primary("play", "Play")));

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
                .firstEmbed();
    }

}
