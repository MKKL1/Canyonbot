package com.mkkl.canyonbot.music.player.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

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
