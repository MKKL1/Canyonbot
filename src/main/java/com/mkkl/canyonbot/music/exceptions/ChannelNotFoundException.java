package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.ExceptionFormatUtils;
import com.mkkl.canyonbot.commands.exceptions.ReplyMessageException;
import com.mkkl.canyonbot.commands.exceptions.UserResponseMessage;
import discord4j.core.object.command.Interaction;

public class ChannelNotFoundException extends UserResponseMessage {
    public ChannelNotFoundException(Interaction interaction) {
        super("User is not connected to channel",
                ExceptionFormatUtils.formatInteraction(interaction) + " Channel not found");
    }
}
