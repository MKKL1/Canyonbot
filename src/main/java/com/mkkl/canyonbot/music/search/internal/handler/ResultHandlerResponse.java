package com.mkkl.canyonbot.music.search.internal.handler;

import com.mkkl.canyonbot.discord.response.Response;
import dev.arbjerg.lavalink.client.player.Track;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResultHandlerResponse {
    private final Response response;
    private final Track track;
}
