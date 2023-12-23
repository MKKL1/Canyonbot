package com.mkkl.canyonbot.music.messages.generators;

import com.mkkl.canyonbot.music.messages.ResponseFormatUtils;
import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.mkkl.canyonbot.music.messages.SearchResponseConst;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import jakarta.annotation.Nullable;
import org.immutables.value.Value;

import java.time.Instant;

@Value.Immutable
public interface ShortPlaylistMessageGenerator extends ResponseMessage {
    AudioPlaylist playlist();

    SearchSource source();

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
        embedBuilder.addField("Track count", String.valueOf(playlist().getTracks().size()), true);
        embedBuilder.addField("Source", source().name(), true);
        //embedBuilder.timestamp(audioTrack.getInfo().isStream ? null : audioTrack.getInfo().creationTime);//TODO add creation time to audio track
        embedBuilder.timestamp(Instant.now());
//        if (audioTrack.getInfo().artworkUrl != null) embedBuilder.image(audioTrack.getInfo().artworkUrl);
        embedBuilder.footer(user().getUsername(), user().getAvatarUrl());

        ResponseMessageData.Builder responseBuilder = ResponseMessageData.builder();
        AudioTrack selectedTrack = playlist().getSelectedTrack();
        if(selectedTrack != null)
            responseBuilder.addEmbed(selectedTrackSpec(selectedTrack));
        responseBuilder.addEmbed(embedBuilder.build());
        return responseBuilder.build();
    }

    private EmbedCreateSpec selectedTrackSpec(AudioTrack selectedTrack) {
        return AudioTrackMessage.builder()
                .audioTrack(selectedTrack)
                .source(source())
                .user(user())
                .build()
                .getMessage()
                .first();
    }

}
