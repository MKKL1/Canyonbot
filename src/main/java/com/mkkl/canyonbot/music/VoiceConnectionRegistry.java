package com.mkkl.canyonbot.music;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class VoiceConnectionRegistry {
    private final Set<Long> guilds = new HashSet<>();

    public void set(long guildId) {
        guilds.add(guildId);
    }

    public void remove(long guildId) {
        guilds.remove(guildId);
    }

    public boolean isSet(long guildId) {
        return guilds.contains(guildId);
    }
}
