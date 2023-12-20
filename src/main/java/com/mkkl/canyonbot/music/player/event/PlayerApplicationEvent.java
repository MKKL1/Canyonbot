package com.mkkl.canyonbot.music.player.event;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class PlayerApplicationEvent extends ApplicationEvent {
    public PlayerApplicationEvent(Object source) {
        super(source);
    }
}
