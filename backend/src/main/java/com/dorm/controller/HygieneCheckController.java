package com.dorm.controller;

import com.dorm.entity.HygieneCheck;
import com.dorm.service.HygieneCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hygiene")
public class HygieneCheckController {

    private final HygieneCheckService hygieneCheckService;

    public HygieneCheckController(HygieneCheckService hygieneCheckService) {
        this.hygieneCheckService = hygieneCheckService;
    }

    @GetMapping
    public ResponseEntity<List<HygieneCheck>> list(@RequestParam(required = false) Long roomId) {
        if (roomId != null) return ResponseEntity.ok(hygieneCheckService.findByRoom(roomId));
        return ResponseEntity.ok(hygieneCheckService.findAll());
    }

    @PostMapping
    public ResponseEntity<HygieneCheck> create(@RequestBody HygieneCheck check) {
        return ResponseEntity.ok(hygieneCheckService.create(check));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hygieneCheckService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
