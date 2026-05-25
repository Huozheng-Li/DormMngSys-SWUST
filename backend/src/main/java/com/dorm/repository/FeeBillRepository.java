package com.dorm.repository;

import com.dorm.entity.FeeBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeeBillRepository extends JpaRepository<FeeBill, Long> {
    List<FeeBill> findByStudentId(Long studentId);

    List<FeeBill> findByStatus(FeeBill.BillStatus status);
}
