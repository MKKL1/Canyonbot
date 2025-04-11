package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.BotExternalException;
import discord4j.core.object.command.Interaction;

public class UserNotInVoiceChannelException extends BotExternalException {
    public UserNotInVoiceChannelException(Interaction interaction) {
        super("User not in voice channel");
    }
}
