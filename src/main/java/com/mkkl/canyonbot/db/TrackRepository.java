package com.mkkl.canyonbot.db;

import com.mkkl.canyonbot.db.entity.TrackEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends CrudRepository<TrackEntity, Long> {
}
