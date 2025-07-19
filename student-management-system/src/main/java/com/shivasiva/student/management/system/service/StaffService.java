package com.shivasiva.student.management.system.service;

import com.shivasiva.student.management.system.model.Staff;
import com.shivasiva.student.management.system.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<Staff> findAll() {
        return staffRepository.findAll();
    }

    public Staff save(Staff staff) {
        return staffRepository.save(staff);
    }

    public Optional<Staff> findByEmail(String email) {
        return staffRepository.findByEmail(email.trim());
    }
}
