package com.mkkl.canyonbot.music.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrackQueueElement {
    private final AudioTrack audioTrack;
    private final User user;
}
