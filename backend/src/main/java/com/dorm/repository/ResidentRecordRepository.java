package com.dorm.repository;

import com.dorm.entity.ResidentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResidentRecordRepository extends JpaRepository<ResidentRecord, Long> {
    List<ResidentRecord> findByStudentId(Long studentId);

    List<ResidentRecord> findByBedId(Long bedId);

    Optional<ResidentRecord> findByStudentIdAndStatus(Long studentId, ResidentRecord.RecordStatus status);

    List<ResidentRecord> findByStatus(ResidentRecord.RecordStatus status);
}
