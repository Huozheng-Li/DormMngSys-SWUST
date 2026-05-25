package com.dorm.controller;

import com.dorm.entity.CheckInOutApplication;
import com.dorm.service.CheckInOutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkinout")
public class CheckInOutController {

    private final CheckInOutService checkInOutService;

    public CheckInOutController(CheckInOutService checkInOutService) {
        this.checkInOutService = checkInOutService;
    }

    @GetMapping
    public ResponseEntity<List<CheckInOutApplication>> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) CheckInOutApplication.ApplicationStatus status) {
        if (studentId != null) {
            return ResponseEntity.ok(checkInOutService.findByStudent(studentId));
        }
        if (status != null) {
            return ResponseEntity.ok(checkInOutService.findByStatus(status));
        }
        return ResponseEntity.ok(checkInOutService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckInOutApplication> get(@PathVariable Long id) {
        return ResponseEntity.ok(checkInOutService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CheckInOutApplication> create(@RequestBody CheckInOutApplication application) {
        return ResponseEntity.ok(checkInOutService.create(application));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<CheckInOutApplication> approve(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Long reviewerId = Long.valueOf(params.get("reviewerId").toString());
        Long bedId = params.get("bedId") != null ? Long.valueOf(params.get("bedId").toString()) : null;
        return ResponseEntity.ok(checkInOutService.approve(id, reviewerId, bedId));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<CheckInOutApplication> reject(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Long reviewerId = Long.valueOf(params.get("reviewerId").toString());
        String comment = (String) params.get("comment");
        return ResponseEntity.ok(checkInOutService.reject(id, reviewerId, comment));
    }
}
