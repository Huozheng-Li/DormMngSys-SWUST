package com.dorm.service;

import com.dorm.entity.VisitorAppointment;
import com.dorm.repository.VisitorAppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitorAppointmentService {

    private final VisitorAppointmentRepository appointmentRepository;

    public VisitorAppointmentService(VisitorAppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<VisitorAppointment> findAll() {
        return appointmentRepository.findAll();
    }

    public List<VisitorAppointment> findByStudent(Long studentId) {
        return appointmentRepository.findByStudentId(studentId);
    }

    public VisitorAppointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
    }

    @Transactional
    public VisitorAppointment create(VisitorAppointment appointment) {
        appointment.setStatus(VisitorAppointment.AppointmentStatus.PENDING);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public VisitorAppointment confirmByStudent(Long id) {
        VisitorAppointment app = findById(id);
        app.setStatus(VisitorAppointment.AppointmentStatus.CONFIRMED);
        return appointmentRepository.save(app);
    }

    @Transactional
    public VisitorAppointment approve(Long id) {
        VisitorAppointment app = findById(id);
        app.setStatus(VisitorAppointment.AppointmentStatus.APPROVED);
        return appointmentRepository.save(app);
    }

    @Transactional
    public VisitorAppointment reject(Long id) {
        VisitorAppointment app = findById(id);
        app.setStatus(VisitorAppointment.AppointmentStatus.REJECTED);
        return appointmentRepository.save(app);
    }

    @Transactional
    public VisitorAppointment recordEntry(Long id) {
        VisitorAppointment app = findById(id);
        app.setEntryTime(LocalDateTime.now());
        return appointmentRepository.save(app);
    }

    @Transactional
    public VisitorAppointment recordExit(Long id) {
        VisitorAppointment app = findById(id);
        app.setExitTime(LocalDateTime.now());
        app.setStatus(VisitorAppointment.AppointmentStatus.COMPLETED);
        return appointmentRepository.save(app);
    }
}
