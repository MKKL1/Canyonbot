package com.mkkl.canyonbot.db;

import com.mkkl.canyonbot.db.entity.GuildEntity;
import com.mkkl.canyonbot.db.entity.TrackEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrackDBService {
    private final TrackRepository trackRepository;
    private final GuildRepository guildRepository;

    public TrackDBService(TrackRepository trackRepository, GuildRepository guildRepository) {
        this.trackRepository = trackRepository;
        this.guildRepository = guildRepository;
    }

    public List<TrackEntity> getLastPlayedTracks(long guildId) {
        Optional<GuildEntity> guildEntity = guildRepository.findById(guildId);
        if(guildEntity.isEmpty()) return new ArrayList<>();
        return trackRepository.findAllByGuildOrderByDate(guildEntity.get(), 5);
    }
}
