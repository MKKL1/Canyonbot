package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.ExceptionFormatUtils;
import com.mkkl.canyonbot.commands.exceptions.ReplyMessageException;
import com.mkkl.canyonbot.commands.exceptions.UserResponseMessage;
import discord4j.core.object.command.Interaction;

public class MemberNotFoundException extends UserResponseMessage {
    public MemberNotFoundException(Interaction interaction) {
        super("Member not found, make sure you use this command in guild context",
                ExceptionFormatUtils.formatInteraction(interaction) + " Member not found");
    }
}
