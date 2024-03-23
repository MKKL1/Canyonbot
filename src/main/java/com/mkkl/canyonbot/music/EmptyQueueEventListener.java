package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.event.MayStopPlayerEvent;
import com.mkkl.canyonbot.music.services.ChannelConnectionService;
import discord4j.common.util.Snowflake;
import org.springframework.stereotype.Component;

@Component
public class EmptyQueueEventListener {

    public EmptyQueueEventListener(EventDispatcher eventDispatcher, ChannelConnectionService channelConnectionService) {
        eventDispatcher.on(MayStopPlayerEvent.class)
                .flatMap(event -> channelConnectionService.leave(Snowflake.of(event.getGuildId())))
                .subscribe();
    }
}
