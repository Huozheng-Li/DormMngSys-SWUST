package com.dorm.controller;

import com.dorm.entity.RepairOrder;
import com.dorm.service.RepairOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repairs")
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    public RepairOrderController(RepairOrderService repairOrderService) {
        this.repairOrderService = repairOrderService;
    }

    @GetMapping
    public ResponseEntity<List<RepairOrder>> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) RepairOrder.OrderStatus status) {
        if (studentId != null) return ResponseEntity.ok(repairOrderService.findByStudent(studentId));
        if (status != null) return ResponseEntity.ok(repairOrderService.findByStatus(status));
        return ResponseEntity.ok(repairOrderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairOrder> get(@PathVariable Long id) {
        return ResponseEntity.ok(repairOrderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RepairOrder> create(@RequestBody RepairOrder order) {
        return ResponseEntity.ok(repairOrderService.create(order));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<RepairOrder> assign(@PathVariable Long id, @RequestBody Map<String, Long> params) {
        return ResponseEntity.ok(repairOrderService.assign(id, params.get("assigneeId")));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<RepairOrder> complete(@PathVariable Long id, @RequestBody Map<String, String> params) {
        return ResponseEntity.ok(repairOrderService.complete(id, params.get("feedback")));
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<RepairOrder> verify(@PathVariable Long id, @RequestBody Map<String, Boolean> params) {
        return ResponseEntity.ok(repairOrderService.verify(id, params.get("passed")));
    }
}
