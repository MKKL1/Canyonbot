package com.mkkl.canyonbot.music.messages.generators;

import com.mkkl.canyonbot.discord.interaction.TempListenableButton;
import com.mkkl.canyonbot.music.messages.ResponseFormatUtils;
import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.messages.ResponseMessageMono;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.mkkl.canyonbot.music.messages.SearchResponseConst;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.DiscordClient;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

//TODO may need to use lombok anyway
@Value.Immutable
public interface ShortPlaylistMessageGenerator extends ResponseMessageMono {
    AudioPlaylist playlist();

    Optional<SearchSource> source();
    Optional<String> query();

    Optional<User> user();
    DiscordClient client();

    @Override
    default ResponseMessageDataMono getMessage() {
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
        if(source().isPresent())
            embedBuilder.addField("Source", source().get().name(), true);
        //embedBuilder.timestamp(audioTrack.getInfo().isStream ? null : audioTrack.getInfo().creationTime);//TODO add creation time to audio track
        embedBuilder.timestamp(Instant.now());
//        if (audioTrack.getInfo().artworkUrl != null) embedBuilder.image(audioTrack.getInfo().artworkUrl);
        if(user().isPresent())
            embedBuilder.footer(user().get().getUsername(), user().get().getAvatarUrl());

        ResponseMessageDataMono.Builder responseBuilder = ResponseMessageDataMono.builder();
        AudioTrack selectedTrack = playlist().getSelectedTrack();
        if (selectedTrack != null)
            responseBuilder.addEmbed(Objects.requireNonNull(selectedTrackSpec(selectedTrack)));
        responseBuilder.addEmbed(embedBuilder.build());

        TempListenableButton button = TempListenableButton.builder(Button.primary("play", "Play"))
                .clickAction((event) -> event.reply("siema"))
                .timeoutAction(() -> Mono.fromRunnable(() -> System.out.println("SIEMA")))
                .build();
        responseBuilder.publisher(button.register(client(), Duration.ofSeconds(5)));
        responseBuilder.addComponent(ActionRow.of(button.getButton()));

        return responseBuilder.build();
    }

    private EmbedCreateSpec selectedTrackSpec(AudioTrack selectedTrack) {
        AudioTrackMessage.Builder builder =  AudioTrackMessage.builder();
        builder.audioTrack(selectedTrack);
        builder.query(query());
        builder.source(source());
        builder.user(user());
        return builder.build().getMessage().firstEmbed();
    }

}
