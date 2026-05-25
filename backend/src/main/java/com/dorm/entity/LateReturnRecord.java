package com.dorm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
public class LateReturnRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(nullable = false)
    private LocalTime recordTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LateStatus status;

    public enum LateStatus {
        LATE_RETURN, NO_RETURN
    }
}
