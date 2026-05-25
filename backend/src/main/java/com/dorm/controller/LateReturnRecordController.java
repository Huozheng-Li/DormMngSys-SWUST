package com.dorm.controller;

import com.dorm.entity.LateReturnRecord;
import com.dorm.service.LateReturnRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/late-returns")
public class LateReturnRecordController {

    private final LateReturnRecordService recordService;

    public LateReturnRecordController(LateReturnRecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping
    public ResponseEntity<List<LateReturnRecord>> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) LateReturnRecord.LateStatus status) {
        if (studentId != null) return ResponseEntity.ok(recordService.findByStudent(studentId));
        if (status != null) return ResponseEntity.ok(recordService.findByStatus(status));
        return ResponseEntity.ok(recordService.findAll());
    }

    @PostMapping
    public ResponseEntity<LateReturnRecord> create(@RequestBody LateReturnRecord record) {
        return ResponseEntity.ok(recordService.create(record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
