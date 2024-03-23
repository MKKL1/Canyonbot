package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.BotInternalException;
import com.mkkl.canyonbot.commands.exceptions.ExceptionFormatUtils;
import discord4j.core.object.command.Interaction;

public class QueryNotFoundException extends BotInternalException {
    public QueryNotFoundException(Interaction interaction) {
        super("Query not found", ExceptionFormatUtils.formatInteraction(interaction) + " query could not be retrieved");
    }
}
