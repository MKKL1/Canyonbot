package com.mkkl.canyonbot.music.messages.generators;

import com.mkkl.canyonbot.commands.exceptions.ResponseMessageText;
import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.messages.SearchResponseConst;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import org.immutables.value.Value;

import java.time.Instant;

@Value.Immutable
public interface ErrorMessageGenerator extends ResponseMessage {
    User user();
    String message();

    @Override
    default ResponseMessageData getMessage() {
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();
        builder.title("Error");
        builder.description(message());
        builder.color(SearchResponseConst.ERROR_COLOR);
        builder.timestamp(Instant.now());
        builder.footer(user().getUsername(), user().getAvatarUrl());
        return ResponseMessageData.builder().addEmbed(builder.build()).build();
    }
}
