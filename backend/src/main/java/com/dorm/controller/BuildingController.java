package com.dorm.controller;

import com.dorm.entity.*;
import com.dorm.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<List<Building>> list() {
        return ResponseEntity.ok(buildingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Building> get(@PathVariable Long id) {
        return ResponseEntity.ok(buildingService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Building> create(@RequestBody Building building) {
        return ResponseEntity.ok(buildingService.create(building));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        buildingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{buildingId}/floors")
    public ResponseEntity<List<Floor>> getFloors(@PathVariable Long buildingId) {
        return ResponseEntity.ok(buildingService.getFloors(buildingId));
    }

    @PostMapping("/{buildingId}/floors")
    public ResponseEntity<Floor> addFloor(@PathVariable Long buildingId, @RequestBody Floor floor) {
        return ResponseEntity.ok(buildingService.addFloor(buildingId, floor));
    }

    @GetMapping("/floors/{floorId}/rooms")
    public ResponseEntity<List<Room>> getRooms(@PathVariable Long floorId) {
        return ResponseEntity.ok(buildingService.getRooms(floorId));
    }

    @PostMapping("/floors/{floorId}/rooms")
    public ResponseEntity<Room> addRoom(@PathVariable Long floorId, @RequestBody Room room) {
        return ResponseEntity.ok(buildingService.addRoom(floorId, room));
    }

    @GetMapping("/rooms/{roomId}/beds")
    public ResponseEntity<List<Bed>> getBeds(@PathVariable Long roomId) {
        return ResponseEntity.ok(buildingService.getBeds(roomId));
    }
}
