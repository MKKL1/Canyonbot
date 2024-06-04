package com.mkkl.canyonbot.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name="tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String uri;
    private Date date;
    private Long userId;
    @ManyToOne
    @JoinColumn(name="guild", nullable = false)
    private GuildEntity guild;
}
