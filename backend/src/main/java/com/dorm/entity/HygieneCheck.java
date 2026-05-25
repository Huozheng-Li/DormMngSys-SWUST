package com.dorm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class HygieneCheck extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id", nullable = false)
    private User inspector;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 500)
    private String comments;

    @Column(length = 500)
    private String imageUrls;

    @Column(nullable = false)
    private LocalDate checkDate;
}
