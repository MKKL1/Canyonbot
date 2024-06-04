package com.mkkl.canyonbot.db;

import com.mkkl.canyonbot.db.entity.GuildEntity;
import com.mkkl.canyonbot.db.entity.TrackEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends CrudRepository<TrackEntity, Long> {

    @Query("SELECT t " +
            "FROM TrackEntity t " +
            "WHERE t.guild = :#{#guild} " +
            "order by t.date " +
            "limit :#{#limit}")
    List<TrackEntity> findAllByGuildOrderByDate(GuildEntity guild, int limit);
}
