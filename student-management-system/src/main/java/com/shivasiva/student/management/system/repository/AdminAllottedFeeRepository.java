package com.shivasiva.student.management.system.repository;

import com.shivasiva.student.management.system.model.AdminAllottedFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminAllottedFeeRepository extends JpaRepository<AdminAllottedFee, Long> {
    List<AdminAllottedFee> findByDepartmentAndSemester(String department, String semester);
    List<AdminAllottedFee> findByDepartment(String department);
}

