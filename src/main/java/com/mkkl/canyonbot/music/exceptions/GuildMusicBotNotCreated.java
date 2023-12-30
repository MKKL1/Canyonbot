package com.mkkl.canyonbot.music.exceptions;

import com.mkkl.canyonbot.commands.exceptions.UserResponseMessage;
import discord4j.core.object.entity.Guild;

public class GuildMusicBotNotCreated extends UserResponseMessage {
    public GuildMusicBotNotCreated(Guild guild) {
        super("No player present. Use /play", "GuildMusicBot for guild " + guild.getName() + " was not created");
    }
}
