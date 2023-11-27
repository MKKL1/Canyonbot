package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.ReplyMessageException;
import lombok.Getter;

@Getter
public class NoMatchException extends ReplyMessageException {
    public NoMatchException(String query) {
        super("No match found for query: " + query);
    }
}
