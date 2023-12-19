package com.mkkl.canyonbot.music.player;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GuildPlaylistMessageService {

    //TODO use redis
    private Map<Guild, Map<Message, AudioPlaylist>> guildMap = new ConcurrentHashMap<>();

    //TODO create container on GuildMusicBot creation event
    public void createContainer(GuildMusicBot guildMusicBot) {
        guildMap.put(guildMusicBot.getGuild(), new ConcurrentHashMap<>());
        //TODO add cleanup
    }

    public void add(Guild guild, Message message, AudioPlaylist audioPlaylist) {
        if(!guildMap.containsKey(guild))
            throw new RuntimeException("Container for guild: " + guild + " not initialized");
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
