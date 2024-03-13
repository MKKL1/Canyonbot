package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.ReplyMessageException;
import dev.arbjerg.lavalink.client.protocol.TrackException;

public class LoadFailedException extends ReplyMessageException {
    public LoadFailedException(String t1, TrackException exception) {
        super("Load failed", "Failed to load track: " + t1 + " " + exception.getMessage());
    }
}
