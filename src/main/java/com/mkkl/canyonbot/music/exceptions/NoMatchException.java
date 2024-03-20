package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.BotExternalException;
import lombok.Getter;

@Getter
public class NoMatchException extends BotExternalException {
    public NoMatchException(String query) {
        super("No match found", "No match found for query: \"" + query + "\"");
    }
}
