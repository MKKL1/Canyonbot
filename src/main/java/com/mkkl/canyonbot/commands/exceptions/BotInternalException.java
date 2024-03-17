package com.mkkl.canyonbot.commands.exceptions;

import lombok.Getter;

/**
 * Helps with responding to command caller with simple message.
 * To be used when something out of caller's control went wrong.
 */
@Getter
public class BotInternalException extends Throwable {
    private final String discordMessage;
    public BotInternalException(Throwable cause, String discordMessage) {
        super(cause);
        this.discordMessage = discordMessage;
    }

    public BotInternalException(String message, String discordMessage) {
        super("Fatal error: " + message + ". Report this issue");
        this.discordMessage = discordMessage;
    }

    public BotInternalException(String discordMessage) {
        this.discordMessage = discordMessage;
    }
}
