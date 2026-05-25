package com.dorm.service;

import com.dorm.entity.LateReturnRecord;
import com.dorm.repository.LateReturnRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LateReturnRecordService {

    private final LateReturnRecordRepository recordRepository;

    public LateReturnRecordService(LateReturnRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public List<LateReturnRecord> findAll() {
        return recordRepository.findAll();
    }

    public List<LateReturnRecord> findByStudent(Long studentId) {
        return recordRepository.findByStudentId(studentId);
    }

    public List<LateReturnRecord> findByStatus(LateReturnRecord.LateStatus status) {
        return recordRepository.findByStatus(status);
    }

    @Transactional
    public LateReturnRecord create(LateReturnRecord record) {
        return recordRepository.save(record);
    }

    @Transactional
    public void delete(Long id) {
        recordRepository.deleteById(id);
    }
}
