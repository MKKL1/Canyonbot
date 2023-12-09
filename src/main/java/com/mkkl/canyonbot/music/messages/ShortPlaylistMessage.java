package com.mkkl.canyonbot.music.messages;

import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder(setterPrefix = "set")
@Getter
public class ShortPlaylistMessage implements ResponseMessage {
    private AudioPlaylist playlist;
    private SearchSource source;
    private User user;

    @Override
    public EmbedCreateSpec getSpec() {
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
        embedBuilder.title(playlist.getName());
        //embedBuilder.url(playlist.getUri()); //TODO add playlist uri
        embedBuilder.color(SearchResponseConst.PLAYLIST_FOUND_COLOR);
        embedBuilder.addField("Duration", ResponseFormatUtils.formatDuration(playlist.getTracks()
                .stream()
                .map(AudioTrack::getDuration)
                .reduce(0L, Long::sum)), true);
        embedBuilder.addField("Track count", String.valueOf(playlist.getTracks().size()), true);
        embedBuilder.addField("Source", source.name(), true);
        //embedBuilder.timestamp(audioTrack.getInfo().isStream ? null : audioTrack.getInfo().creationTime);//TODO add creation time to audio track
        embedBuilder.timestamp(Instant.now());
//        if (audioTrack.getInfo().artworkUrl != null) embedBuilder.image(audioTrack.getInfo().artworkUrl);
        embedBuilder.footer(user.getUsername(), user.getAvatarUrl());
        return embedBuilder.build();
    }

    //TODO remove button used to play playlist after it has been clicked
    public ActionRow getActionRow() {
//        //TODO subscribing here is weird
//        TempListenableButton playButton = TempListenableButton.builder(Button.primary("play-playlist", "Play playlist"))
//                .setButtonClickAction(event -> event.edit("siema"))
//                .register(SearchResponseConst.BUTTON_TIMEOUT).subscribe();
//        return ActionRow.of(playButton);
        return null;
    }
}
