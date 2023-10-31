package com.mkkl.canyonbot.music.messages;

import discord4j.core.spec.EmbedCreateSpec;

public interface ResponseMessage {
    EmbedCreateSpec getSpec();
}
