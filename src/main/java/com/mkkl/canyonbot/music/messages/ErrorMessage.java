package com.mkkl.canyonbot.music.messages;

import com.mkkl.canyonbot.commands.exceptions.ResponseMessageText;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;

import java.time.Instant;

public class ErrorMessage implements ResponseMessage {
    EmbedCreateSpec embedCreateSpec;
    private ErrorMessage(User user, String message) {
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();
        builder.title("Error");
        builder.description(message);
        builder.color(SearchResponseConst.ERROR_COLOR);
        builder.timestamp(Instant.now());
        builder.footer(user.getUsername(), user.getAvatarUrl());
        embedCreateSpec = builder.build();
    }

    public static ErrorMessage of(User user, String message) {
        return new ErrorMessage(user, message);
    }

    public static ErrorMessage of(User user, ResponseMessageText message) {
        return new ErrorMessage(user, message.getText());
    }

    @Override
    public EmbedCreateSpec getSpec() {
        return embedCreateSpec;
    }
}
