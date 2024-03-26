package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.event.MayDestroyPlayerEvent;
import com.mkkl.canyonbot.music.player.event.lavalink.PlayerUpdateEvent;
import com.mkkl.canyonbot.music.services.PlayerService;
import discord4j.common.util.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MayDestroyPlayerEventListener {

    //TODO custom annotation for event listeners?
    public MayDestroyPlayerEventListener(EventDispatcher eventDispatcher, PlayerService playerService) {
        eventDispatcher.on(MayDestroyPlayerEvent.class)
                .flatMap(event -> playerService.leaveChannel(Snowflake.of(event.getGuildId())))
                .subscribe();
    }
}
