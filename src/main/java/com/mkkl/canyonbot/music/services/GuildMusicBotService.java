package com.mkkl.canyonbot.music.services;

import com.mkkl.canyonbot.music.player.LinkContext;
import com.mkkl.canyonbot.music.player.MusicBotEventDispatcher;
import com.mkkl.canyonbot.music.player.MusicPlayerBaseFactory;
import com.mkkl.canyonbot.music.player.event.PlayerEventPublisher;
import com.mkkl.canyonbot.music.player.queue.SimpleTrackQueue;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates and saves music bot instance for each guild
 */
@Service
public class GuildMusicBotService {
    private final MusicPlayerBaseFactory musicPlayerBaseFactory;
    private final PlayerEventPublisher publisher;
    private final Map<Guild, LinkContext> guildMusicBotMap = new ConcurrentHashMap<>();

    public GuildMusicBotService(MusicPlayerBaseFactory musicPlayerBaseFactory, PlayerEventPublisher publisher) {
        this.musicPlayerBaseFactory = musicPlayerBaseFactory;
        this.publisher = publisher;
    }

    //TODO this is more of a Repository responsibility than Service
    public LinkContext createGuildMusicBot(Guild guild) {
        if(guildMusicBotMap.containsKey(guild))
            return guildMusicBotMap.get(guild); //TODO there is no feedback whether or not GuildMusicBot is created
        MusicPlayerBase musicPlayerBase = musicPlayerBaseFactory.create();
        LinkContext linkContext = LinkContext.builder()
                .guild(guild)
                .eventDispatcher(MusicBotEventDispatcher.create())
                .player(musicPlayerBase)
                .trackQueue(new SimpleTrackQueue())
                .build();
        musicPlayerBase.registerEvents(linkContext.getGuild(), linkContext.getEventDispatcher());
        guildMusicBotMap.put(guild, linkContext);
        publisher.publish(new GuildPlayerCreationEvent(this, linkContext));
        return linkContext;
    }

    public Optional<LinkContext> getGuildMusicBot(Guild guild) {
        return Optional.ofNullable(guildMusicBotMap.get(guild));
    }
}
