package com.mkkl.canyonbot.music.player;

import com.mkkl.canyonbot.music.player.queue.SimpleTrackQueue;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GuildMusicBotService {
    private final MusicPlayerBaseFactory musicPlayerBaseFactory;
    private final Map<Guild, GuildMusicBot> guildMusicBotMap = new ConcurrentHashMap<>();

    public GuildMusicBotService(MusicPlayerBaseFactory musicPlayerBaseFactory) {
        this.musicPlayerBaseFactory = musicPlayerBaseFactory;
    }

    //TODO this is more of a Repository responsibility than Service
    public GuildMusicBot createGuildMusicBot(Guild guild) {
        MusicPlayerBase musicPlayerBase = musicPlayerBaseFactory.create();
        GuildMusicBot guildMusicBot = GuildMusicBot.builder()
                .guild(guild)
                .eventDispatcher(MusicBotEventDispatcher.create())
                .player(musicPlayerBase)
                .trackQueue(new SimpleTrackQueue())
                .build();
        guildMusicBotMap.put(guild, guildMusicBot);
        return guildMusicBot;
    }

    public Optional<GuildMusicBot> getGuildMusicBot(Guild guild) {
        return Optional.ofNullable(guildMusicBotMap.get(guild));
    }
}
