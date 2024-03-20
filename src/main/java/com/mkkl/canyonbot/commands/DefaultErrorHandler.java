package com.mkkl.canyonbot.commands;

import com.mkkl.canyonbot.commands.exceptions.BotInternalException;
import com.mkkl.canyonbot.commands.exceptions.BotExternalException;
import com.mkkl.canyonbot.music.messages.generators.ErrorMessage;
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
        if (throwable instanceof BotInternalException botInternalException) {
            respond = true;
            message = botInternalException.getDiscordMessage();
            log.error("Error was caught in stream", botInternalException);
        } else if (throwable instanceof BotExternalException userResponseMessage) {
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
