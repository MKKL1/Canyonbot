package com.mkkl.canyonbot.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="guilds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuildEntity {
    @Id
    private Long id;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL)
    private Set<TrackEntity> tracks = new HashSet<>();
}
