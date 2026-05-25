package com.dorm.controller;

import com.dorm.entity.VisitorAppointment;
import com.dorm.service.VisitorAppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/visitors")
public class VisitorAppointmentController {

    private final VisitorAppointmentService visitorAppointmentService;

    public VisitorAppointmentController(VisitorAppointmentService visitorAppointmentService) {
        this.visitorAppointmentService = visitorAppointmentService;
    }

    @GetMapping
    public ResponseEntity<List<VisitorAppointment>> list(
            @RequestParam(required = false) Long studentId) {
        if (studentId != null) return ResponseEntity.ok(visitorAppointmentService.findByStudent(studentId));
        return ResponseEntity.ok(visitorAppointmentService.findAll());
    }

    @PostMapping
    public ResponseEntity<VisitorAppointment> create(@RequestBody VisitorAppointment appointment) {
        return ResponseEntity.ok(visitorAppointmentService.create(appointment));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<VisitorAppointment> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(visitorAppointmentService.confirmByStudent(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<VisitorAppointment> approve(@PathVariable Long id) {
        return ResponseEntity.ok(visitorAppointmentService.approve(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<VisitorAppointment> reject(@PathVariable Long id) {
        return ResponseEntity.ok(visitorAppointmentService.reject(id));
    }

    @PutMapping("/{id}/entry")
    public ResponseEntity<VisitorAppointment> recordEntry(@PathVariable Long id) {
        return ResponseEntity.ok(visitorAppointmentService.recordEntry(id));
    }

    @PutMapping("/{id}/exit")
    public ResponseEntity<VisitorAppointment> recordExit(@PathVariable Long id) {
        return ResponseEntity.ok(visitorAppointmentService.recordExit(id));
    }
}
