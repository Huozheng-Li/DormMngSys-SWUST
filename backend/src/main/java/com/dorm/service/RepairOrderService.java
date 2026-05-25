package com.dorm.service;

import com.dorm.entity.RepairOrder;
import com.dorm.entity.User;
import com.dorm.repository.RepairOrderRepository;
import com.dorm.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;
    private final UserRepository userRepository;

    public RepairOrderService(RepairOrderRepository repairOrderRepository, UserRepository userRepository) {
        this.repairOrderRepository = repairOrderRepository;
        this.userRepository = userRepository;
    }

    public List<RepairOrder> findAll() {
        return repairOrderRepository.findAll();
    }

    public RepairOrder findById(Long id) {
        return repairOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));
    }

    public List<RepairOrder> findByStudent(Long studentId) {
        return repairOrderRepository.findByStudentId(studentId);
    }

    public List<RepairOrder> findByStatus(RepairOrder.OrderStatus status) {
        return repairOrderRepository.findByStatus(status);
    }

    @Transactional
    public RepairOrder create(RepairOrder order) {
        order.setStatus(RepairOrder.OrderStatus.PENDING);
        return repairOrderRepository.save(order);
    }

    @Transactional
    public RepairOrder assign(Long id, Long assigneeId) {
        RepairOrder order = findById(id);
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("维修工不存在"));
        order.setAssignee(assignee);
        order.setStatus(RepairOrder.OrderStatus.ASSIGNED);
        return repairOrderRepository.save(order);
    }

    @Transactional
    public RepairOrder complete(Long id, String feedback) {
        RepairOrder order = findById(id);
        order.setFeedback(feedback);
        order.setStatus(RepairOrder.OrderStatus.COMPLETED);
        return repairOrderRepository.save(order);
    }

    @Transactional
    public RepairOrder verify(Long id, Boolean passed) {
        RepairOrder order = findById(id);
        order.setVerified(passed);
        order.setStatus(passed ? RepairOrder.OrderStatus.VERIFIED : RepairOrder.OrderStatus.ASSIGNED);
        return repairOrderRepository.save(order);
    }
}
