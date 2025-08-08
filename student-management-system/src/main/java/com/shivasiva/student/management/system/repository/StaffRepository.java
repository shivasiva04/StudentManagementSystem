package com.shivasiva.student.management.system.repository;

import com.shivasiva.student.management.system.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByEmailIgnoreCase(String email);


    @Modifying
    @Transactional
    @Query("UPDATE Staff s SET s.registered = true WHERE s.email = :email")
    void markAsRegisteredByEmail(String email);

    Optional<Staff> findByEmailIgnoreCaseOrNameIgnoreCase(String email, String name);
}
