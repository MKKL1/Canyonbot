package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.ReplyMessageException;
import com.mkkl.canyonbot.commands.exceptions.UserResponseMessage;
import lombok.Getter;

@Getter
public class NoMatchException extends UserResponseMessage {
    public NoMatchException(String query) {
        super("No match found", "No match found for query: \"" + query + "\"");
    }
}
