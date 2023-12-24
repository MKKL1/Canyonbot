package com.mkkl.canyonbot.commands.exceptions;

import discord4j.core.object.command.Interaction;
import lombok.Getter;

/**
 * Helps with responding to command caller with simple message.
 * To be used when something out of caller's control went wrong.
 */
@Getter
public class ReplyMessageException extends Throwable {
    private final String discordMessage;
    public ReplyMessageException(Throwable cause, String discordMessage) {
        super(cause);
        this.discordMessage = discordMessage;
    }

    public ReplyMessageException(String message, String discordMessage) {
        super("Fatal error: " + message + ". Report this issue");
        this.discordMessage = discordMessage;
    }

    public ReplyMessageException(String discordMessage) {
        this.discordMessage = discordMessage;
    }
}
