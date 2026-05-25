package com.dorm.repository;

import com.dorm.entity.LateReturnRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LateReturnRecordRepository extends JpaRepository<LateReturnRecord, Long> {
    List<LateReturnRecord> findByStudentId(Long studentId);

    List<LateReturnRecord> findByStatus(LateReturnRecord.LateStatus status);
}
