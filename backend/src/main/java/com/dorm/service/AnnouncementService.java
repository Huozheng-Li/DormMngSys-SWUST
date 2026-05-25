package com.dorm.service;

import com.dorm.entity.*;
import com.dorm.repository.AnnouncementReadRepository;
import com.dorm.repository.AnnouncementRepository;
import com.dorm.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementReadRepository readRepository;
    private final UserRepository userRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository,
                               AnnouncementReadRepository readRepository,
                               UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.readRepository = readRepository;
        this.userRepository = userRepository;
    }

    public List<Announcement> findAll() {
        return announcementRepository.findAll();
    }

    public Announcement findById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
    }

    @Transactional
    public Announcement create(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    @Transactional
    public void markAsRead(Long announcementId, Long userId) {
        if (!readRepository.existsByAnnouncementIdAndUserId(announcementId, userId)) {
            AnnouncementRead read = new AnnouncementRead();
            read.setAnnouncement(findById(announcementId));
            read.setUser(userRepository.findById(userId).orElseThrow());
            read.setReadAt(LocalDateTime.now());
            readRepository.save(read);
        }
    }

    public void delete(Long id) {
        announcementRepository.deleteById(id);
    }

    public Map<String, Object> getReadStatus(Long announcementId) {
        List<AnnouncementRead> reads = readRepository.findByAnnouncementId(announcementId);
        Map<String, Object> result = new HashMap<>();
        result.put("total", reads.size());
        result.put("users", reads.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("userId", r.getUser().getId());
            m.put("userName", r.getUser().getName());
            m.put("readAt", r.getReadAt());
            return m;
        }).toList());
        return result;
    }
}
