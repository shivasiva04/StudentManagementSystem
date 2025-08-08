package com.shivasiva.student.management.system.repository;

import com.shivasiva.student.management.system.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByEmail(String email);
}
