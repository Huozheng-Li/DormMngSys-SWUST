package com.dorm.repository;

import com.dorm.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
    List<RepairOrder> findByStudentId(Long studentId);

    List<RepairOrder> findByAssigneeId(Long assigneeId);

    List<RepairOrder> findByStatus(RepairOrder.OrderStatus status);
}
