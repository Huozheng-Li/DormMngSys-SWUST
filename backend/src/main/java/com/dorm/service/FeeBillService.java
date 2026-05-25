package com.dorm.service;

import com.dorm.entity.FeeBill;
import com.dorm.repository.FeeBillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class FeeBillService {

    private final FeeBillRepository feeBillRepository;

    public FeeBillService(FeeBillRepository feeBillRepository) {
        this.feeBillRepository = feeBillRepository;
    }

    public List<FeeBill> findAll() {
        return feeBillRepository.findAll();
    }

    public List<FeeBill> findByStudent(Long studentId) {
        return feeBillRepository.findByStudentId(studentId);
    }

    public List<FeeBill> findByStatus(FeeBill.BillStatus status) {
        return feeBillRepository.findByStatus(status);
    }

    public FeeBill findById(Long id) {
        return feeBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账单不存在"));
    }

    @Transactional
    public FeeBill create(FeeBill bill) {
        bill.setStatus(FeeBill.BillStatus.UNPAID);
        return feeBillRepository.save(bill);
    }

    @Transactional
    public FeeBill pay(Long id, String method, String transactionId) {
        FeeBill bill = findById(id);
        if (bill.getStatus() == FeeBill.BillStatus.PAID) {
            throw new RuntimeException("该账单已缴费");
        }
        bill.setStatus(FeeBill.BillStatus.PAID);
        bill.setPaidAt(LocalDate.now());
        bill.setPaymentMethod(method);
        bill.setTransactionId(transactionId);
        return feeBillRepository.save(bill);
    }

    @Transactional
    public void delete(Long id) {
        feeBillRepository.deleteById(id);
    }
}
