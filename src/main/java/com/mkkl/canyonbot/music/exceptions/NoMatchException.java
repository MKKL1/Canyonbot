package com.mkkl.canyonbot.music.exceptions;

import lombok.Getter;

@Getter
public class NoMatchException extends RuntimeException implements ResponseMessageText {
    private final String query;
    public NoMatchException(String query) {
        super("No match found for query: " + query);
        this.query = query;
    }

    @Override
    public String getText() {
        return "No match found for query: " + query;
    }
}
