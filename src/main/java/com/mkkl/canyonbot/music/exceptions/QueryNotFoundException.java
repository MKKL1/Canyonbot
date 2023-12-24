package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.ExceptionFormatUtils;
import com.mkkl.canyonbot.commands.exceptions.ReplyMessageException;
import discord4j.core.object.command.Interaction;

public class QueryNotFoundException extends ReplyMessageException {
    public QueryNotFoundException(Interaction interaction) {
        super("Query not found", ExceptionFormatUtils.formatInteraction(interaction) + " query could not be retrieved");
    }
}
