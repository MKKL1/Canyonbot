package com.mkkl.canyonbot.commands.exceptions;

import lombok.Getter;

/**
 * Used to easily terminate execution of command and respond to caller with provided message.
 */
@Getter
public class BotExternalException extends Throwable {
    private final String discordMessage;
    public BotExternalException(String discordMessage, String detailedMessage) {
        super(detailedMessage);
        this.discordMessage = discordMessage;
    }

    public BotExternalException(String discordMessage) {
        super(discordMessage);
        this.discordMessage = discordMessage;
    }
}
