package com.mkkl.canyonbot.music.services;

import com.mkkl.canyonbot.music.player.event.GuildPlayerCreationEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class GuildPlaylistMessageService {

    //TODO use redis
    //TODO set max per guild
    //TODO automatic cleanup
    private final Map<Guild, Map<Message, AudioPlaylist>> guildMap = new ConcurrentHashMap<>();

    @EventListener
    private void createContainer(GuildPlayerCreationEvent event) {
        guildMap.put(event.getGuildMusicBot().getGuild(), new ConcurrentHashMap<>());
        log.info("Created playlist container for " + event.getGuildMusicBot().getGuild().getName());
    }

    public void add(Guild guild, Message message, AudioPlaylist audioPlaylist) {
        if(!guildMap.containsKey(guild))
            throw new RuntimeException("Container for guild: " + guild.getName() + " not initialized");
        guildMap.get(guild).put(message, audioPlaylist);
    }

    public Optional<AudioPlaylist> get(Guild guild, Message message) {
        if(!guildMap.containsKey(guild)) return Optional.empty();
        return Optional.ofNullable(guildMap.get(guild).get(message));
    }

    public void remove(Guild guild, Message message) {
        if(!guildMap.containsKey(guild))
            throw new RuntimeException("Container for guild: " + guild + " not initialized");
        guildMap.get(guild).remove(message);
    }
}
