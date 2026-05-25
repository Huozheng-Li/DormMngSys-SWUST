package com.dorm.repository;

import com.dorm.entity.AnnouncementRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnnouncementReadRepository extends JpaRepository<AnnouncementRead, Long> {
    List<AnnouncementRead> findByAnnouncementId(Long announcementId);

    boolean existsByAnnouncementIdAndUserId(Long announcementId, Long userId);
}
