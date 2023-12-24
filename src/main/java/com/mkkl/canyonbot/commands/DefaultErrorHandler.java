package com.mkkl.canyonbot.commands;

import com.mkkl.canyonbot.commands.exceptions.ReplyMessageException;
import com.mkkl.canyonbot.commands.exceptions.UserResponseMessage;
import com.mkkl.canyonbot.music.messages.generators.ErrorMessage;
import com.mkkl.canyonbot.music.messages.generators.ErrorMessageGenerator;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DefaultErrorHandler implements CommandErrorHandler {
    @Override
    public Mono<Void> handle(Throwable throwable, ChatInputInteractionEvent event) {
        //TODO add identifier to thrown errors so that full tracelog could be found easier in log files
        //TODO there needs to be a distinction between user and application caused errors

        boolean respond = false;
        String message = "";
        if (throwable instanceof ReplyMessageException replyMessageException) {
            respond = true;
            message = replyMessageException.getDiscordMessage();
            log.error("Error was caught in stream", replyMessageException);
        } else if (throwable instanceof UserResponseMessage userResponseMessage) {
            respond = true;
            message = userResponseMessage.getDiscordMessage();
            log.debug("Error was caught in stream: " + userResponseMessage.getMessage());
        }

        if(respond) {
            return event.createFollowup(InteractionFollowupCreateSpec.builder()
                            .addAllEmbeds(ErrorMessage.builder()
                                    .user(event.getInteraction().getUser())
                                    .message(message)
                                    .build()
                                    .getMessage()
                                    .embeds())
                            .build())
                    .then();
        }

        //Handler of last resort
        log.error("Error was caught in stream", throwable);
        return event.reply("Error:" + throwable.getMessage())
                .then(); //TODO log error, and send short message to user
    }
}
