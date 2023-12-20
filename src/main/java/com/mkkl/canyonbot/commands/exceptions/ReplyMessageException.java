package com.mkkl.canyonbot.commands.exceptions;

//TODO this should probably be decorator
public class ReplyMessageException extends RuntimeException implements ResponseMessageText {
    private final String text;
    public ReplyMessageException(String text) {
        super(text);
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
