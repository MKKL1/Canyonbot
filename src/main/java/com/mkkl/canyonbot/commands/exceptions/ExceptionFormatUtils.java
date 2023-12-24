package com.mkkl.canyonbot.commands.exceptions;

import discord4j.core.object.command.Interaction;

public class ExceptionFormatUtils {
    public static String formatInteraction(Interaction interaction) {
        return "[" +
                "Id:" + interaction.getId() + "," +
                "ChannelId:" + interaction.getChannelId() + "," +
                "User:" + interaction.getUser().getUsername() +
                "]";
    }
}
