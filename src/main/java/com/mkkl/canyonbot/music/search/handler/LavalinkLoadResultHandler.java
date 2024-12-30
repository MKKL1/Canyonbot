package com.mkkl.canyonbot.music.search.handler;

import com.mkkl.canyonbot.music.commands.PlayCommand;
import dev.arbjerg.lavalink.client.player.LavalinkLoadResult;

public interface LavalinkLoadResultHandler<T extends LavalinkLoadResult> {
    ResultHandlerResponse handle(PlayCommand.Context context, T searchResult);
}
