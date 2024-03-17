package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.BotExternalException;

public class SourceNotFoundException extends BotExternalException {
    public SourceNotFoundException(String sourceId) {
        super("Source by id \"" + sourceId + "\" not found");
    }
}
