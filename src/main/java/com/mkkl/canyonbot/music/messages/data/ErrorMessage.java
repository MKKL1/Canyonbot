package com.mkkl.canyonbot.music.messages.data;

import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.music.messages.ResponseMessage;
import com.mkkl.canyonbot.music.messages.SearchResponseConst;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class ErrorMessage implements ResponseMessage {
    @NonNull
    User user;
    @NonNull
    String message;

    @Override
    public Response getMessage() {
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();
        builder.title("Error");
        builder.description(message);
        builder.color(SearchResponseConst.ERROR_COLOR);
        builder.timestamp(Instant.now());
        builder.footer(user.getUsername(), user.getAvatarUrl());
        return Response.builder().embeds(List.of(builder.build())).build();
    }
}
