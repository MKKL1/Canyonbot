package com.mkkl.canyonbot.music.exceptions;

public class LinkContextNotFound extends RuntimeException {
    public LinkContextNotFound() {
        super("LinkContext for guild was not found");
    }

    public LinkContextNotFound(String message) {
        super(message);
    }
}
