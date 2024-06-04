package com.mkkl.canyonbot.db;

import com.mkkl.canyonbot.db.entity.GuildEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuildRepository extends JpaRepository<GuildEntity, Long> {
    default GuildEntity findByIdOrCreate(Long guildId) {
        return findById(guildId).orElseGet(() -> {
            GuildEntity guild = GuildEntity.builder()
                    .id(guildId)
                    .build();
            save(guild);
            return guild;
        });
    }
}
