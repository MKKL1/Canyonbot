package com.mkkl.canyonbot.music.player.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

//Event manager used for publishing events to all services
//For example when music bot is created to serve some guild, all additional services acknowledge this fact
//TODO unify names of events handlers to better represent what they do
@Component
public class PlayerEventPublisher {
    private final ApplicationEventPublisher publisher;

    public PlayerEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(PlayerApplicationEvent playerApplicationEvent) {
        publisher.publishEvent(playerApplicationEvent);
    }
}
