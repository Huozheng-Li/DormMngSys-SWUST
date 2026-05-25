package com.dorm.repository;

import com.dorm.entity.CheckInOutApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CheckInOutApplicationRepository extends JpaRepository<CheckInOutApplication, Long> {
    List<CheckInOutApplication> findByStudentId(Long studentId);

    List<CheckInOutApplication> findByStatus(CheckInOutApplication.ApplicationStatus status);
}
