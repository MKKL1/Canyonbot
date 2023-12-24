package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.ExceptionFormatUtils;
import com.mkkl.canyonbot.commands.exceptions.ReplyMessageException;
import com.mkkl.canyonbot.commands.exceptions.UserResponseMessage;
import discord4j.core.object.command.Interaction;

public class InvalidAudioChannelException extends UserResponseMessage {
    public InvalidAudioChannelException(Interaction interaction) {
        super("Channel you are connected to is not an Audio Channel",
                ExceptionFormatUtils.formatInteraction(interaction) + "Cannot cast to Audio Channel");
    }
}
