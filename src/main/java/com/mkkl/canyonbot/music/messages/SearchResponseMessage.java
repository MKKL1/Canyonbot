package com.mkkl.canyonbot.music.messages;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionReplyEditSpec;

public interface SearchResponseMessage {
    EmbedCreateSpec getSpecResponse();
}
