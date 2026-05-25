package com.dorm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class VisitorAppointment extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String visitorName;

    @Column(nullable = false, length = 20)
    private String visitorPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime visitTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    public enum AppointmentStatus {
        PENDING, CONFIRMED, APPROVED, REJECTED, COMPLETED
    }
}
