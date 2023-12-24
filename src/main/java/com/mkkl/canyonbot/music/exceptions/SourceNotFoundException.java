package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.ExceptionFormatUtils;
import com.mkkl.canyonbot.commands.exceptions.UserResponseMessage;
import discord4j.core.object.command.Interaction;

public class SourceNotFoundException extends UserResponseMessage {
    public SourceNotFoundException(String sourceId) {
        super("Source by id \"" + sourceId + "\" not found");
    }
}
