package com.dorm.controller;

import com.dorm.entity.Announcement;
import com.dorm.service.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public ResponseEntity<List<Announcement>> list() {
        return ResponseEntity.ok(announcementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Announcement> get(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Announcement> create(@RequestBody Announcement announcement) {
        return ResponseEntity.ok(announcementService.create(announcement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id, @RequestBody Map<String, Long> params) {
        announcementService.markAsRead(id, params.get("userId"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/read-status")
    public ResponseEntity<Map<String, Object>> getReadStatus(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.getReadStatus(id));
    }
}
