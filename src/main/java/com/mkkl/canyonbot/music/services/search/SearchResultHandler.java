package com.mkkl.canyonbot.music.services.search;

import com.mkkl.canyonbot.music.commands.PlayCommand;
import dev.arbjerg.lavalink.client.protocol.LavalinkLoadResult;
import reactor.core.publisher.Mono;

public interface SearchResultHandler<T extends LavalinkLoadResult> {
    Mono<?> handle(PlayCommand.Context context, T searchResult);
}
