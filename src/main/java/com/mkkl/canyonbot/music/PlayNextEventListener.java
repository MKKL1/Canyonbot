package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.db.GuildRepository;
import com.mkkl.canyonbot.db.TrackRepository;
import com.mkkl.canyonbot.db.entity.GuildEntity;
import com.mkkl.canyonbot.db.entity.TrackEntity;
import com.mkkl.canyonbot.event.EventDispatcher;
import com.mkkl.canyonbot.music.player.event.scheduler.PlayNextEvent;
import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class PlayNextEventListener {

    //TODO custom annotation for event listeners?
    public PlayNextEventListener(EventDispatcher eventDispatcher, TrackRepository trackRepository, GuildRepository guildRepository) {
        eventDispatcher.on(PlayNextEvent.class)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(event -> {
                    GuildEntity guildEntity = guildRepository.findByIdOrCreate(event.getGuildId());
                    TrackInfo trackInfo = event.getTrack()
                            .getTrack()
                            .getInfo();

                    TrackEntity trackEntity = TrackEntity.builder()
                            .uri(trackInfo.getUri())
                            .title(trackInfo.getTitle())
                            .userId(event.getTrack().getUser().getId().asLong())
                            .date(Date.from(Instant.now()))
                            .guild(guildEntity)
                            .build();
                    trackRepository.save(trackEntity);
                })
                .subscribe();
    }
}