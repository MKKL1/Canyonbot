package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.queue.TrackQueue;
import com.mkkl.canyonbot.music.player.queue.TrackQueueElement;
import discord4j.core.object.entity.Guild;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

//TODO rename to GuildMusicSession?
@AllArgsConstructor
@Getter
@Builder
public class GuildMusicBot {
    private final MusicPlayerBase player;
    private final MusicBotEventDispatcher eventDispatcher;
    private final TrackQueue trackQueue;//TODO remove it from here
    private final Guild guild;
}
