package com.mkkl.canyonbot.commands;

import com.mkkl.canyonbot.commands.exceptions.ReplyMessageException;
import com.mkkl.canyonbot.music.messages.generators.ErrorMessage;
import com.mkkl.canyonbot.music.messages.generators.ErrorMessageGenerator;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DefaultErrorHandler implements CommandErrorHandler {
    @Override
    public Mono<Void> handle(Throwable throwable, ChatInputInteractionEvent event) {
        //TODO add identifier to thrown errors so that full tracelog could be found easier in log files
        //TODO there needs to be a distinction between user and application caused errors
        if (throwable instanceof ReplyMessageException)
            return event.createFollowup(InteractionFollowupCreateSpec.builder()
                            .addAllEmbeds(ErrorMessage.builder()
                                    .user(event.getInteraction()
                                            .getUser())
                                    .message(throwable.getMessage())
                                    .build()
                                    .getMessage()
                                    .embeds())
                            .build())
                    .then();
        //Handler of last resort
        throwable.printStackTrace();
        return event.reply("Error:" + throwable.getMessage())
                .then(); //TODO log error, and send short message to user
    }
}
