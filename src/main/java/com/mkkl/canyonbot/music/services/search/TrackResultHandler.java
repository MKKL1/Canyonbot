package com.mkkl.canyonbot.music.services.search;

import com.mkkl.canyonbot.music.commands.PlayCommand;
import com.mkkl.canyonbot.music.messages.generators.AudioTrackMessage;
import com.mkkl.canyonbot.music.search.SearchResult;
import com.mkkl.canyonbot.music.services.PlayTrackService;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TrackResultHandler implements SearchResultHandler {
    @Autowired
    private PlayTrackService playTrackService;
    @Override
    public Mono<?> handle(PlayCommand.Context context, SearchResult searchResult) {
        assert searchResult.getTracks() != null;
        AudioTrack track = searchResult.getTracks().getFirst();
        //First adds track to queue and then sends confirmation message
        return playTrackService.playTrack(context.getGuild(), context.getChannel(), context.getEvent().getInteraction(), track)
                .then(context.getEvent().createFollowup(InteractionFollowupCreateSpec.builder()
                        .addAllEmbeds(AudioTrackMessage.builder()
                                .audioTrack(track)
                                .source(searchResult.getSource())
                                .query(context.getQuery().orElseThrow(() -> new IllegalStateException("Query not found")))
                                .user(context.getEvent().getInteraction().getUser())
                                .build()
                                .getMessage().embeds())
                        .build()));
    }
}
