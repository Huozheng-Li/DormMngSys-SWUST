package com.dorm.repository;

import com.dorm.entity.VisitorAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VisitorAppointmentRepository extends JpaRepository<VisitorAppointment, Long> {
    List<VisitorAppointment> findByStudentId(Long studentId);

    List<VisitorAppointment> findByStatus(VisitorAppointment.AppointmentStatus status);
}
