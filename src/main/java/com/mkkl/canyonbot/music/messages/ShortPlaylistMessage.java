package com.mkkl.canyonbot.music.messages;

import com.mkkl.canyonbot.interaction.TempListenableButton;
import com.mkkl.canyonbot.music.search.internal.sources.SearchSource;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionCallbackSpec;
import lombok.Builder;
import lombok.Getter;
import reactor.core.publisher.Mono;

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

    public ActionRow getActionRow(DiscordClient client) {
        Button playButton = Button.primary("play-playlist", "Play playlist");
        //TODO subscribing here is weird
        new PlayPlaylistButton(playButton).register(client, SearchResponseConst.BUTTON_TIMEOUT).subscribe();
        return ActionRow.of(playButton);
    }

    private class PlayPlaylistButton extends TempListenableButton {

        public PlayPlaylistButton(Button button) {
            super(button);
        }

        @Override
        public Mono<Void> onButtonPress(ButtonInteractionEvent event) {
            //TODO load playlist to queue
            //TODO and maybe add some permission check
            return event.reply("Loaded playlist " + playlist.getName());
        }
    }
}
