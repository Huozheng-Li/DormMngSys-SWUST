package com.dorm.service;

import com.dorm.entity.*;
import com.dorm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    public BuildingService(BuildingRepository buildingRepository,
                           FloorRepository floorRepository,
                           RoomRepository roomRepository,
                           BedRepository bedRepository) {
        this.buildingRepository = buildingRepository;
        this.floorRepository = floorRepository;
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
    }

    public List<Building> findAll() {
        return buildingRepository.findAll();
    }

    public Building findById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("楼栋不存在"));
    }

    public Building create(Building building) {
        return buildingRepository.save(building);
    }

    public void delete(Long id) {
        buildingRepository.deleteById(id);
    }

    @Transactional
    public Floor addFloor(Long buildingId, Floor floor) {
        Building building = findById(buildingId);
        floor.setBuilding(building);
        return floorRepository.save(floor);
    }

    public List<Floor> getFloors(Long buildingId) {
        return floorRepository.findByBuildingId(buildingId);
    }

    @Transactional
    public Room addRoom(Long floorId, Room room) {
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new RuntimeException("楼层不存在"));
        room.setFloor(floor);
        Room saved = roomRepository.save(room);

        for (int i = 1; i <= room.getCapacity(); i++) {
            Bed bed = new Bed();
            bed.setRoom(saved);
            bed.setBedNumber(String.valueOf(i));
            bed.setStatus(Bed.BedStatus.FREE);
            bedRepository.save(bed);
        }
        return saved;
    }

    public List<Room> getRooms(Long floorId) {
        return roomRepository.findByFloorId(floorId);
    }

    public List<Bed> getBeds(Long roomId) {
        return bedRepository.findByRoomId(roomId);
    }
}
