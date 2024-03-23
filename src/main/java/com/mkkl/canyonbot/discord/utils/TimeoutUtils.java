package com.mkkl.canyonbot.discord.utils;

import discord4j.core.object.entity.Message;
import discord4j.discordjson.possible.Possible;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;

public class TimeoutUtils {
    public static Mono<Void> clearActionBar(Message message) {
        return message.edit().withComponents(Possible.of(Optional.of(Collections.emptyList())))
                .then();
    }
}
