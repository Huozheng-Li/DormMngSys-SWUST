package com.dorm.repository;

import com.dorm.entity.HygieneCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HygieneCheckRepository extends JpaRepository<HygieneCheck, Long> {
    List<HygieneCheck> findByRoomId(Long roomId);

    List<HygieneCheck> findByInspectorId(Long inspectorId);
}
