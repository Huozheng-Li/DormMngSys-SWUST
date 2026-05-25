package com.dorm.controller;

import com.dorm.entity.FeeBill;
import com.dorm.service.FeeBillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fees")
public class FeeBillController {

    private final FeeBillService feeBillService;

    public FeeBillController(FeeBillService feeBillService) {
        this.feeBillService = feeBillService;
    }

    @GetMapping
    public ResponseEntity<List<FeeBill>> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) FeeBill.BillStatus status) {
        if (studentId != null) return ResponseEntity.ok(feeBillService.findByStudent(studentId));
        if (status != null) return ResponseEntity.ok(feeBillService.findByStatus(status));
        return ResponseEntity.ok(feeBillService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeeBill> get(@PathVariable Long id) {
        return ResponseEntity.ok(feeBillService.findById(id));
    }

    @PostMapping
    public ResponseEntity<FeeBill> create(@RequestBody FeeBill bill) {
        return ResponseEntity.ok(feeBillService.create(bill));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<FeeBill> pay(@PathVariable Long id, @RequestBody Map<String, String> params) {
        return ResponseEntity.ok(feeBillService.pay(id, params.get("method"), params.get("transactionId")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feeBillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
