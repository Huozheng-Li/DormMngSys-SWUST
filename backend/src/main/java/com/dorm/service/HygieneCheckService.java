package com.dorm.service;

import com.dorm.entity.HygieneCheck;
import com.dorm.repository.HygieneCheckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class HygieneCheckService {

    private final HygieneCheckRepository hygieneCheckRepository;

    public HygieneCheckService(HygieneCheckRepository hygieneCheckRepository) {
        this.hygieneCheckRepository = hygieneCheckRepository;
    }

    public List<HygieneCheck> findAll() {
        return hygieneCheckRepository.findAll();
    }

    public List<HygieneCheck> findByRoom(Long roomId) {
        return hygieneCheckRepository.findByRoomId(roomId);
    }

    @Transactional
    public HygieneCheck create(HygieneCheck check) {
        return hygieneCheckRepository.save(check);
    }

    @Transactional
    public void delete(Long id) {
        hygieneCheckRepository.deleteById(id);
    }
}
