package com.dorm.service;

import com.dorm.entity.*;
import com.dorm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class CheckInOutService {

    private final CheckInOutApplicationRepository applicationRepository;
    private final ResidentRecordRepository residentRecordRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    public CheckInOutService(CheckInOutApplicationRepository applicationRepository,
                             ResidentRecordRepository residentRecordRepository,
                             UserRepository userRepository,
                             RoomRepository roomRepository,
                             BedRepository bedRepository) {
        this.applicationRepository = applicationRepository;
        this.residentRecordRepository = residentRecordRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
    }

    public List<CheckInOutApplication> findAll() {
        return applicationRepository.findAll();
    }

    public List<CheckInOutApplication> findByStudent(Long studentId) {
        return applicationRepository.findByStudentId(studentId);
    }

    public List<CheckInOutApplication> findByStatus(CheckInOutApplication.ApplicationStatus status) {
        return applicationRepository.findByStatus(status);
    }

    public CheckInOutApplication findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("申请不存在"));
    }

    @Transactional
    public CheckInOutApplication create(CheckInOutApplication application) {
        application.setStatus(CheckInOutApplication.ApplicationStatus.PENDING);
        return applicationRepository.save(application);
    }

    @Transactional
    public CheckInOutApplication approve(Long id, Long reviewerId, Long bedId) {
        CheckInOutApplication app = findById(id);
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("审核人不存在"));

        app.setStatus(CheckInOutApplication.ApplicationStatus.APPROVED);
        app.setReviewer(reviewer);
        app.setReviewComment("审核通过");

        if (app.getType() == CheckInOutApplication.ApplicationType.CHECK_IN) {
            Bed bed = bedRepository.findById(bedId)
                    .orElseThrow(() -> new RuntimeException("床位不存在"));
            if (bed.getStatus() != Bed.BedStatus.FREE) {
                throw new RuntimeException("该床位已被占用");
            }
            bed.setStatus(Bed.BedStatus.OCCUPIED);
            bedRepository.save(bed);

            ResidentRecord record = new ResidentRecord();
            record.setStudent(app.getStudent());
            record.setBed(bed);
            record.setCheckInDate(LocalDate.now());
            record.setStatus(ResidentRecord.RecordStatus.ACTIVE);
            residentRecordRepository.save(record);

            updateRoomStatus(bed.getRoom().getId());
        } else {
            ResidentRecord record = residentRecordRepository
                    .findByStudentIdAndStatus(app.getStudent().getId(), ResidentRecord.RecordStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("未找到有效的入住记录"));
            record.setCheckOutDate(LocalDate.now());
            record.setStatus(ResidentRecord.RecordStatus.CHECKED_OUT);
            residentRecordRepository.save(record);

            Bed bed = record.getBed();
            bed.setStatus(Bed.BedStatus.FREE);
            bedRepository.save(bed);

            updateRoomStatus(bed.getRoom().getId());
        }

        return applicationRepository.save(app);
    }

    @Transactional
    public CheckInOutApplication reject(Long id, Long reviewerId, String comment) {
        CheckInOutApplication app = findById(id);
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("审核人不存在"));
        app.setStatus(CheckInOutApplication.ApplicationStatus.REJECTED);
        app.setReviewer(reviewer);
        app.setReviewComment(comment);
        return applicationRepository.save(app);
    }

    private void updateRoomStatus(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("房间不存在"));
        long occupied = bedRepository.countByRoomIdAndStatus(roomId, Bed.BedStatus.OCCUPIED);
        long maintenance = bedRepository.countByRoomIdAndStatus(roomId, Bed.BedStatus.MAINTENANCE);
        long total = room.getCapacity();

        if (occupied == 0 && maintenance == 0) {
            room.setStatus(Room.RoomStatus.AVAILABLE);
        } else if (occupied == total) {
            room.setStatus(Room.RoomStatus.FULL);
        } else {
            room.setStatus(Room.RoomStatus.PARTIAL);
        }
        roomRepository.save(room);
    }
}
