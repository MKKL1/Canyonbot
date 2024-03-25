package com.mkkl.canyonbot.music.search.internal.handler;

import com.mkkl.canyonbot.music.commands.PlayCommand;
import dev.arbjerg.lavalink.client.protocol.LavalinkLoadResult;
import reactor.core.publisher.Mono;

public interface LavalinkLoadResultHandler<T extends LavalinkLoadResult> {
    ResultHandlerResponse handle(PlayCommand.Context context, T searchResult);
}
