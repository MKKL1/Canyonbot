package com.mkkl.canyonbot.music.services;

import com.mkkl.canyonbot.music.player.GuildMusicBot;
import com.mkkl.canyonbot.music.player.MusicBotEventDispatcher;
import com.mkkl.canyonbot.music.player.MusicPlayerBase;
import com.mkkl.canyonbot.music.player.MusicPlayerBaseFactory;
import com.mkkl.canyonbot.music.player.event.GuildPlayerCreationEvent;
import com.mkkl.canyonbot.music.player.event.PlayerEventPublisher;
import com.mkkl.canyonbot.music.player.queue.SimpleTrackQueue;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GuildMusicBotService {
    private final MusicPlayerBaseFactory musicPlayerBaseFactory;
    private final PlayerEventPublisher publisher;
    private final Map<Guild, GuildMusicBot> guildMusicBotMap = new ConcurrentHashMap<>();

    public GuildMusicBotService(MusicPlayerBaseFactory musicPlayerBaseFactory, PlayerEventPublisher publisher) {
        this.musicPlayerBaseFactory = musicPlayerBaseFactory;
        this.publisher = publisher;
    }

    //TODO this is more of a Repository responsibility than Service
    public GuildMusicBot createGuildMusicBot(Guild guild) {
        if(guildMusicBotMap.containsKey(guild))
            return guildMusicBotMap.get(guild); //TODO there is no feedback whether or not GuildMusicBot is created
        MusicPlayerBase musicPlayerBase = musicPlayerBaseFactory.create();
        GuildMusicBot guildMusicBot = GuildMusicBot.builder()
                .guild(guild)
                .eventDispatcher(MusicBotEventDispatcher.create())
                .player(musicPlayerBase)
                .trackQueue(new SimpleTrackQueue())
                .build();
        musicPlayerBase.registerEvents(guildMusicBot.getGuild(), guildMusicBot.getEventDispatcher());
        guildMusicBotMap.put(guild, guildMusicBot);
        publisher.publish(new GuildPlayerCreationEvent(this, guildMusicBot));
        return guildMusicBot;
    }

    public Optional<GuildMusicBot> getGuildMusicBot(Guild guild) {
        return Optional.ofNullable(guildMusicBotMap.get(guild));
    }
}
