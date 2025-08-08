package com.shivasiva.student.management.system.repository;

import com.shivasiva.student.management.system.model.Fee;
import com.shivasiva.student.management.system.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findByStudent_DepartmentAndStudent_Semester(String department, String semester);
    List<Fee> findByStudent(Student student);
    List<Fee> findByStudentAndPaid(Student student, boolean paid);
}
